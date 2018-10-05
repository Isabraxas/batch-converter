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
    @Autowired
    private StatementDetailService statementDetailService;

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

        HeaderPayload statementHeader = new HeaderPayload();

        final int[] i = {0};
        while ((line = b.readLine()) != null) {
            bytesRead += line.length();

            if (i[0] > 100) {
                CommonUtils.showPercentageByBytes(filePath, bytesRead);
                i[0] = 0;
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
                    if (!startReadDetails) {
                        CommonProcessFileService.setTotalAmount(line, statementHeader);
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

                saveStatement(filePath, statement, statementHeader, detailList);

                statement = new StatementPayload();
                statementHeader = new HeaderPayload();
                detailList = new ArrayList<DetailPayload>();
                startReadDetails = false;
                addHeader = true;
            }

            i[0]++;
        }

        b.close();
        System.out.print("\n");
        return fileInfoResponse;
    }


    private void saveStatement(final String filePath, final StatementPayload statement,
                               final HeaderPayload statementHeader, final List<DetailPayload> detailList) throws
        IOException, NoSuchAlgorithmException {
        log.debug("Starting save statement function");
        HeaderPayload headerPayload = this.statementHeaderService.getStatementHeaderPayload(
            statementHeader);

        //Make hash and set to StatementHeader
        String hash = HashCode.getCodigoHash(filePath);
        statementHeader.setFileHash(hash);

        //Check if this header is null
        if (statement.getHeader() != null) {
            //Check if exist this header
            if (statementHeaderService.exist(statement.getHeader())) {
                fileInfoResponse.incrementDuplicatedHeaders();
                log.warn("This header already exist: " + statementHeader.toString());

                //Delete headers and details related
                if (headerPayload != null) {
                    if (!HashCode.areEqualsFileAndHash(filePath, headerPayload.getFileHash())) {
                        log.warn("Deleting this header: " + headerPayload.toString());
                        this.statementHeaderService.delete(statementHeader);
                        fileInfoResponse.incremenDuplicatedDetails(headerPayload.getDetailPayloads().size());
                    }
                }
            }

            if (!statementHeaderService.exist(statementHeader)) {
                log.info("Saving Statements data");
                statementHeaderService.insertOneInToDatabase(statementHeader, detailList);
                fileInfoResponse.incrementInsertedHeaders();
                fileInfoResponse.incrementInsertedDetails(detailList.size());
            }
        }

        log.debug("Ending save statement function");
    }
}
