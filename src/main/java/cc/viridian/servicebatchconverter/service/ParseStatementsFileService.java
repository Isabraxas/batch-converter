package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import cc.viridian.servicebatchconverter.utils.CommonUtils;
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
public class ParseStatementsFileService {

    FileInfoResponse fileInfoResponse = new FileInfoResponse();

    @Autowired
    private StatementHeaderService statementHeaderService;
    @Autowired
    private StatementDetailService statementDetailService;

    Long currentLine = 0L;
    Long rateLines = 10L;
    Long totalLines = 0L;
    Integer countStatements = 0;
    CommonUtils commonUtils = new CommonUtils();

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
        totalLines = commonUtils.getFileLines(filePath);

        while ((line = b.readLine()) != null) {

            DetailPayload detail = new DetailPayload();
            currentLine++;
            updateProgress();
            //System.out.print(", " + currentLine);
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
                    log.debug("HEADER: " + statementHeader);
                }
                statement.setDetails(detailList);
            } catch (StringIndexOutOfBoundsException se) {
                log.error(se.getMessage());
                statement.setHeader(null);
                addHeader = false;
            }

            if (line.contains("-----------------")) {

                saveStatement(filePath, statement, statementHeader, detailList);

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

    private void updateProgress() {
        if (currentLine.toString().equals(rateLines.toString())) {
            //TODO llamar a la funcion util para incrementar la barra de progreso y reiniciar el contador
            commonUtils.incrementProgressBar(currentLine, totalLines);
            System.out.println("ESTADO: " + commonUtils.getProgressBar() + "%");
            rateLines = currentLine + 11;
        }
    }

    private void saveStatement(final String filePath, final StatementPayload statement
        , final HeaderPayload statementHeader, final List<DetailPayload> detailList) throws
        IOException, NoSuchAlgorithmException {
        log.debug("Starting save statement function");
        HeaderPayload headerPayload = this.statementHeaderService.getStatementHeaderPayload(
            statementHeader);

        //Make hash and set to statemetHeader
        String hash = HashCode.getCodigoHash(filePath);
        statementHeader.setFileHash(hash);

        //Check if exist this details
        detailList.stream().forEach(detailP -> {
            if (statementDetailService.exist(detailP)) {
                fileInfoResponse.incrementDuplicatedDetails();
                log.warn("This detail already exist: " + detailP.toString());
            }
        });

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
