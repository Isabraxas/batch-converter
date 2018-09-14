package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.utils.FormatUtil;
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
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ReadStatementsFileService {

    @Autowired
    private StatementHeaderService statementHeaderService;
    @Autowired
    private StatementDetailService statementDetailService;

    public FileInfoResponse readContent(final String filePath)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);

        FileInfoResponse fileInfoResponse = new FileInfoResponse();
        String line;

        Integer currentLine = 0;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        Integer i = 0;
        Boolean startReadDetails = false;
        Boolean addHeader = true;
        Integer colSum = 0;

        HeaderPayload statementHeader = new HeaderPayload();
        System.out.print("LINE:");

        //TODO crear un hash para el archivo y verificar si existe o no
        String hashCodeFile = HashCode.getCodigoHash(filePath);
        Boolean isSaved = this.statementHeaderService.existFileHash(hashCodeFile);


        if (isSaved) {
            log.warn("This file alredy is saved");
        }else {
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

                            detail = CommonProcessFileService.fillStatementAccountLog(line, detail, statementHeader);

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
                    log.error("Error while reading the file on the line :" + currentLine);
                    log.error(e.getMessage());
                    statement.setHeader(null);
                    addHeader = false;
                }

                if (line.contains("-----------------")) {

                    statement = new StatementPayload();
                    statementHeader = new HeaderPayload();
                    detailList = new ArrayList<DetailPayload>();
                    startReadDetails = false;
                    addHeader = true;
                }
            }
        }

        fileInfoResponse.setHashExist(isSaved);
        b.close();

        System.out.print("\n");
        return fileInfoResponse;
    }

}
