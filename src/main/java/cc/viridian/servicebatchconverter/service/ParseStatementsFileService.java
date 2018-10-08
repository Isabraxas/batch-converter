package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.utils.CommonUtils;
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
public class ParseStatementsFileService {

    FileInfoResponse fileInfoResponse = new FileInfoResponse();

    @Value("${config.separator.statement}")
    private String separatorStatement;

    @Autowired
    private StatementHeaderService statementHeaderService;

    private String hash;

    Long currentLine = 0L;
    long bytesRead = 0;

    public FileInfoResponse parseContent(final String filePath)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);
        String line;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        Boolean startReadDetails = false;
        Boolean addHeader = true;

        CommonUtils.setTotalBytes(filePath);

        HeaderPayload statementHeader = new HeaderPayload();

        //Get current file hash code
        hash = HashCode.getCodigoHash(filePath);

        int currentLine = 0;
        while ((line = b.readLine()) != null) {
            bytesRead += line.length();

            if (currentLine % 500 == 0) {
                CommonUtils.showPercentageByBytes(bytesRead);
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
                        CommonProcessFileService.setBalanceEnd(line, statementHeader);
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

        //Check if this header is null
        if (statement.getHeader() != null) {
            //Check if exist this header
            if (statementHeaderService.exist(statement.getHeader())) {
                fileInfoResponse.incrementDuplicatedHeaders();
                log.warn("This header already exist: " + header.toString());

                //Delete headers and details related
                if (headerDB != null) {
                    if (!header.getFileHash().equals(headerDB.getFileHash())) {
                        log.warn("Deleting this header: " + headerDB.toString());
                        this.statementHeaderService.delete(headerDB);
                        fileInfoResponse.incremenDuplicatedDetails(headerDB.getDetailPayloads().size());
                    }
                }
            }

            if (!statementHeaderService.exist(header)) {
                log.info("Saving Statements data");
                statementHeaderService.insertOneInToDatabase(header, detailList);
                fileInfoResponse.incrementInsertedHeaders();
                fileInfoResponse.incrementInsertedDetails(detailList.size());
            }
        }

        log.debug("Ending save statement function");
    }
}
