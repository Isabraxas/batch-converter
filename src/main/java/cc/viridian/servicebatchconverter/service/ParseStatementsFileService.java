package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.FileInfoResponse;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
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
public class ParseStatementsFileService {

    @Autowired
    private StatementHeaderService statementHeaderService;
    @Autowired
    private StatementDetailService statementDetailService;

    public FileInfoResponse parseContent(final String filePath)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);
        FileInfoResponse fileInfoResponse = new FileInfoResponse();

        String line;

        Integer currentLine = 0;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();
        Integer i = 0;
        Boolean startReadDetails = false;
        Boolean addHeader = true;
        Integer colSum = 0;

        HeaderPayload statementHeader = new HeaderPayload();
        System.out.print("LINE:");

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
            } catch (StringIndexOutOfBoundsException se) {
                log.error(se.getMessage());
                statement.setHeader(null);
                addHeader = false;
            }
            if (line.contains("-----------------")) {

                HeaderPayload headerPayload = this.statementHeaderService.getStatementHeaderPayload(
                    statementHeader);

                //TODO crear un hash para el archivo y guardarlo en el header statement_title
                String hash = HashCode.getCodigoHash(filePath);
                statementHeader.setFileHash(hash);

                //TODO comprobar si ya existe alguno de los details
                detailList.stream().forEach(detailP -> {
                    if (statementDetailService.exist(detailP)) {
                        fileInfoResponse.incrementDuplicatedDetails();
                        log.warn("El statement detail ya existe: " + detailP.toString());
                    }
                });

                //TODO comprobar si ya existe el header
                if (statement.getHeader() != null) {
                    //TODO comprobar si ya existe alguno de los headers
                    if (statementHeaderService.exist(statement.getHeader())) {
                        fileInfoResponse.incrementDuplicatedHeaders();
                        log.warn("El statement header ya existe: " + statementHeader.toString());
                        //TODO eliminar header y detail si el archivo nuevo(distinto hash) contiene el mismo header
                        if (headerPayload != null) {
                            if (!HashCode.areEqualsFileAndHash(filePath, headerPayload.getFileHash())) {
                                this.statementHeaderService.delete(statementHeader);
                                log.warn("Se ha eliminado este header: " + headerPayload.toString());
                            }
                        }
                    }
                }

                //TODO guardar en la base de datos
                if (statement.getHeader() != null) {
                    if (!statementHeaderService.exist(statementHeader)) {
                        statementHeaderService.insertOneInToDatabase(statementHeader, detailList);
                        fileInfoResponse.incrementReplacedHeaders();
                        fileInfoResponse.incrementReplacedDetails(detailList.size());
                    } else {
                        log.error("El statement header ya existe: " + statementHeader.toString());
                        //TODO comprobar si ya existe alguno de los details y guardarlos si no existen
                        HeaderPayload finalStatementHeader = statementHeader;
                        detailList.stream().forEach(detailP -> {
                            if (statementDetailService.exist(detailP)) {
                                //Comentado por recurrencia
                                //statementDetailService.insertOneInToDatabase(detailP, finalStatementHeader);
                                log.warn("El statement detail ya existe: " + detailP.toString());
                            }
                        });
                    }
                }

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
