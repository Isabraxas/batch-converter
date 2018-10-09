package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CheckStatementsFileService {
    @Value("${config.separator.statement}")
    private String separatorStatement;

    public FileInfoResponse readContent(final String filePath, final Userlog userlog)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);

        FileInfoResponse fileInfoResponse = new FileInfoResponse();
        String line;

        Integer currentLine = 0;
        CommonUtils.getInitTime();  //todo: move below?

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        Boolean startReadDetails = false;
        Boolean addHeader = true;
        Boolean fileIsFine = true;

        HeaderPayload statementHeader = new HeaderPayload();
        HeaderPayload errorHeader = new HeaderPayload();

        int countErrorHeader = 0;

        while ((line = b.readLine()) != null) {

            DetailPayload detail = new DetailPayload();
            currentLine++;
            //System.out.print(", " + currentLine);
            try {
                if (!line.contains(separatorStatement) && !line.equals("")) {

                    //System.out.println(line);

                    try {
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
                    } catch (Exception e) {
                        log.error("Error while reading the file on the line :" + currentLine
                                      + " account-code ---> " + statementHeader.getAccountCode());
                        userlog.error("Error while reading the file on the line :" + currentLine
                                          + " account-code ---> " + statementHeader.getAccountCode());
                        fileInfoResponse.incrementErrorDetails();
                        if (!errorHeader.equals(statementHeader)) {
                            fileInfoResponse.incrementErrorHeaders();
                            errorHeader = statementHeader;
                        }
                    }

                    //Set Total amount
                    //CommonProcessFileService.setBalanceEnd(line, statementHeader);
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
                userlog.error("Error while reading the file on the line :" + currentLine
                                  + " account-code ---> " + statementHeader.getAccountCode());
                log.error(e.getMessage());
                statement.setHeader(null);
                addHeader = false;
                fileIsFine = false;
            }

            if (line.contains(separatorStatement)) {

                statement = new StatementPayload();
                statementHeader = new HeaderPayload();
                detailList = new ArrayList<DetailPayload>();
                startReadDetails = false;
                addHeader = true;
            }
        }
        b.close();

        System.out.print("\n");
        return fileInfoResponse;
    }
}
