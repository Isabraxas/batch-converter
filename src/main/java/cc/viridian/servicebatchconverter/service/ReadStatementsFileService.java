package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ReadStatementsFileService {

    @Autowired
    private StatementHeaderService statementHeaderService;
    @Autowired
    private ParseStatementsFileService parseStatementsFileService;

    public FileInfoResponse readContent(final String filePath)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);

        FileInfoResponse fileInfoResponse = new FileInfoResponse();
        String line;

        Integer currentLine = 0;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        Boolean startReadDetails = false;
        Boolean addHeader = true;
        Boolean fileIsFine = true;

        HeaderPayload statementHeader = new HeaderPayload();

        //Make hash file and check if exist
        String hashCodeFile = HashCode.getCodigoHash(filePath);
        Boolean isSaved = this.statementHeaderService.existFileHash(hashCodeFile);

        if (isSaved) {
            log.warn("This file alredy is saved");
        } else {
            while ((line = b.readLine()) != null) {

                DetailPayload detail = new DetailPayload();
                currentLine++;
                System.out.print(", " + currentLine);
                try {
                    if (!line.contains("-----------------") && !line.equals("")) {

                        //System.out.println(line);

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
                        //System.out.println("HEADER: " + statementHeader);
                    }
                    statement.setDetails(detailList);
                    //System.out.println("DETAILS: " + detailList);
                } catch (Exception e) {
                    System.out.println();
                    log.error("Error while reading the file on the line :" + currentLine
                                  + " account-code ---> " + statementHeader.getAccountCode());
                    log.error(e.getMessage());
                    statement.setHeader(null);
                    addHeader = false;
                    fileIsFine = false;
                }

                if (line.contains("-----------------")) {

                    statement = new StatementPayload();
                    statementHeader = new HeaderPayload();
                    detailList = new ArrayList<DetailPayload>();
                    startReadDetails = false;
                    addHeader = true;
                }
            }
            b.close();

            //if(fileIsFine) {//<--- Cuando el archivo esta corrupto y no se debe guardar nada
            if (true) {
                log.info("Saving Statements");
                parseStatementsFileService.parseContent(filePath);
            }
        }

        fileInfoResponse.setHashExist(isSaved);
        System.out.print("\n");
        return fileInfoResponse;
    }
}
