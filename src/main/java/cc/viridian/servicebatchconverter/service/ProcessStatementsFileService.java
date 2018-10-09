package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.utils.CommonUtils;
import cc.viridian.servicebatchconverter.writer.Userlog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProcessStatementsFileService {

    FileInfoResponse fileInfoResponse = new FileInfoResponse();

    @Value("${config.separator.statement}")
    private String separatorStatement;

    @Autowired
    private StatementHeaderService statementHeaderService;

    private String hash;

    long bytesRead = 0;

    Userlog userlog;

    public FileInfoResponse parseContent(final String filePath, final Userlog userlog)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);
        String line;

        this.userlog = userlog;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        Boolean startReadDetails = false;
        Boolean addHeader = true;

        CommonUtils.setTotalBytes(filePath);
        System.out.println("parse content");
        System.out.println(CommonUtils.getCurrentRunTime());
        CommonUtils.getInitTime();

        HeaderPayload statementHeader = new HeaderPayload();

        //Get current file hash code
        hash = HashCode.getCodigoHash(filePath);

        System.out.println("hash:");
        System.out.println(CommonUtils.getCurrentRunTime());
        CommonUtils.getInitTime();

        int currentLine = 0;
        int currentStatement = 0;
        while ((line = b.readLine()) != null) {
            bytesRead += line.length();

            if (currentLine % 1000 == 0) {
                CommonUtils.showPercentageByBytes(bytesRead, currentStatement);
            }

            DetailPayload detail = new DetailPayload();
            currentLine++;

            try {

                if (!line.contains(separatorStatement) && !line.equals("")) {
                    //Try fill the Header
                    statementHeader = CommonProcessFileService.fillStatementAccountHeader(line, statementHeader);

                    //Set size columns and return if start read details lines
                    startReadDetails = CommonProcessFileService
                        .setSizeColumnsOfStatementAccountDetailHeader(line, startReadDetails);

                    //Fill statement details
                    if (startReadDetails) {
                        detail = CommonProcessFileService
                            .fillStatementDetailAccountRecord(line, detail, statementHeader);
                        if (detail != null) {
                            detailList.add(detail);
                        }
                    }

                    //Set Total amount
                    if (startReadDetails) {
                        statementHeader.setBalanceEnd(CommonProcessFileService.getBalanceEnd(line));
                    }
                }

                if (addHeader) {
                    statement.setHeader(statementHeader);
                    log.debug("HEADER: " + statementHeader);
                }
                statement.setDetails(detailList);
            } catch (StringIndexOutOfBoundsException se) {
                log.error(se.getMessage());
                statement.setHeader(null);
                addHeader = false;
            }

            if (line.contains(separatorStatement)) {
                if (addHeader) {
                    currentStatement++;
                    BigDecimal balanceEnd = CommonProcessFileService.verifyBalanceEnd(statement);
                    statement.getHeader().setBalanceEnd(balanceEnd);
                }
                saveStatement(filePath, statement, statementHeader, detailList);

                statement = new StatementPayload();
                statementHeader = new HeaderPayload();
                detailList = new ArrayList<DetailPayload>();
                startReadDetails = false;
                addHeader = true;
            }

            currentLine++;
        }

        b.close();
        System.out.print("\n");
        return fileInfoResponse;
    }

    private void saveStatement(final String filePath, final StatementPayload statement,
                               final HeaderPayload header, final List<DetailPayload> detailList) throws
        IOException, NoSuchAlgorithmException {
        log.debug("Starting save statement function");
        HeaderPayload headerDB = this.statementHeaderService.getStatementHeaderPayload(
            header);

        //Set file hash code in header
        header.setFileHash(hash);

        boolean existHeaderDB = headerDB != null;

        //Check if this header is null
        if (statement.getHeader() != null) {
            //Check if exist this header
            if (existHeaderDB) {

                if (!header.getFileHash().equals(headerDB.getFileHash())) {
                    fileInfoResponse.incrementDuplicatedHeaders();
                    log.warn("This header already exist: " + header.toString());

                    log.warn("Deleting this header: " + headerDB.toString());
                    userlog.warn("Deleting this header: "
                                     + "account: " + headerDB.getAccountCode()
                                     + ", customer: " + headerDB.getCustomerCode()
                                     + ", date_from: " + headerDB.getDateFrom()
                                     + ", date_to: " + headerDB.getDateTo()
                    );
                    this.statementHeaderService.delete(headerDB);
                    fileInfoResponse.incremenDuplicatedDetails(headerDB.getDetailPayloads().size());
                    existHeaderDB = !existHeaderDB;
                }
            }

            if (!existHeaderDB) {
                log.info("Saving Statements data");
                statementHeaderService.insertOneInToDatabase(header, detailList);
                fileInfoResponse.incrementInsertedHeaders();
                fileInfoResponse.incrementInsertedDetails(detailList.size());
            }
        }

        log.debug("Ending save statement function");
    }
}
