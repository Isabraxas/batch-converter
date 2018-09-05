package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.Utils.FormatUtil;
import cc.viridian.servicebatchconverter.hash.HashCode;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
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
public class ParseStatementsFileService {

    @Autowired
    private StatementHeaderService statementHeaderService;

    Integer dateSize = null;
    Integer descSize = null;
    Integer refSize = null;
    Integer amountSize = null;
    Integer operationSize = null;

    public List<StatementPayload> parseContent(final String filePath)
        throws FileNotFoundException, IOException, NoSuchAlgorithmException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);

        String line;

        Integer currentLine = 0;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();
        Integer i = 0;
        Boolean startReadDetails = false;
        Integer colSum = 0;

        HeaderPayload statementHeader = new HeaderPayload();
        System.out.print("LINE:");

        while ((line = b.readLine()) != null) {

            DetailPayload detail = new DetailPayload();
            currentLine++;
            System.out.print(", " + currentLine);

            if (!line.contains("-----------------") && !line.equals("")) {

                //System.out.println(line);

                //Try fill the Header
                statementHeader = this.fillStatementAccountHeader(line, statementHeader);

                //Set size columns and return if start read details lines
                startReadDetails = this.setSizeColumnsOfStatementAccountDetailHeader(line, startReadDetails);

                //Fill statement details
                if (startReadDetails) {
                    detail = this.fillStatementAccountLog(line, detail, statementHeader);
                    if (detail != null) {
                        detailList.add(detail);
                    }
                }

                //Set Total amount
                if (!startReadDetails) {
                    this.setTotalAmount(line, statementHeader);
                }
            }

            statement.setHeader(statementHeader);
            //System.out.println("HEADER: " + statementHeader);
            statement.setDetails(detailList);
            //System.out.println("DETAILS: " + detailList);

            if (line.contains("-----------------")) {

                //TODO crear un hash para el archivo y guardarlo en el header statement_title
                String hash= HashCode.getCodigoHash(filePath);
                statementHeader.setFileHash(hash);

                //statementPayloadList.add(statement);

                //TODO guardar en la base de datos
                if(!statementHeaderService.exist(statementHeader)) {
                    statementHeaderService.insertOneInToDatabase(statementHeader, detailList);
                }else {
                    log.error("El statement header ya existe: "+ statementHeader.toString());
                }

                //TODO compareFileWithFile hash del archivo con los hash ya lmacenados en la base de datos
                //TODO si se repite lazar una exception y no guardar nada.
                statement = new StatementPayload();
                statementHeader = new HeaderPayload();
                detailList = new ArrayList<DetailPayload>();
                startReadDetails = false;
            }
        }
        b.close();

        System.out.print("\n");
        return statementPayloadList;
    }

    private HeaderPayload fillStatementAccountHeader(final String line, final HeaderPayload headerPayload) {
        HeaderPayload statementHeader = headerPayload;
        String[] splitLine;

        if (line.contains("Bank")) {

            splitLine = line.split(": ");
            statementHeader.setAccountBranch(FormatUtil.parseToNull(splitLine[1]));
        }

        if (line.contains("Address")) {

            splitLine = line.split(": ");
            statementHeader.setAccountAddress(FormatUtil.parseToNull(splitLine[1]));
        }

        if (line.contains("Statement")) {

            if (!line.contains("null")) {
                splitLine = line.split(": ");
                String[] dates = splitLine[1].split(" - ");
                String[] dateForm = dates[0].split("-");
                String[] dateTo = dates[1].split("-");

                statementHeader.setDateFrom(LocalDate.of(Integer.valueOf(dateForm[0]),
                                                         Integer.valueOf(dateForm[1]),
                                                         Integer.valueOf(dateForm[2])
                ));

                statementHeader.setDateTo(LocalDate.of(Integer.valueOf(dateTo[0]),
                                                       Integer.valueOf(dateTo[1]),
                                                       Integer.valueOf(dateTo[2])
                ));
            } else {
                statementHeader.setDateFrom(null);
                statementHeader.setDateTo(null);
            }
        }

        if (line.contains("Customer")) {

            splitLine = line.split(": ");
            statementHeader.setCustomerCode(FormatUtil.parseToNull(splitLine[1]));
        }

        if (line.contains("Account")) {

            splitLine = line.split(": ");
            statementHeader.setAccountCode(FormatUtil.parseToNull(splitLine[1]));
        }

        return statementHeader;
    }

    private Boolean setSizeColumnsOfStatementAccountDetailHeader(final String line, final Boolean startReadDetails) {
        Boolean rStartReadDetails;
        if (line.contains("Date:")) {
            dateSize = 20;
            descSize = 90;
            refSize = 20;
            amountSize = 20;
            operationSize = 15;

            //startDetailsLine= currentLine+1;
            rStartReadDetails = true;
        } else {
            rStartReadDetails = startReadDetails;
        }

        return rStartReadDetails;
    }

    private DetailPayload fillStatementAccountLog(final String line,
                                                  final DetailPayload detailPayload,
                                                  final HeaderPayload headerPayload) {
        DetailPayload detail = detailPayload;
        HeaderPayload statementHeader = headerPayload;
        Integer colSum = 0;

        if (!line.contains("Total") && !line.contains("Date:")) {

            if (!line.equals("")) {
                colSum = 0;

                //Date
                String colDate = line.substring(colSum, colSum + dateSize);
                detail.setDate(colDate.split(" ")[0]);
                //LocalDateTime
                String[] sptDate = colDate.split(" ")[0].split("-");
                String[] sptTime = colDate.split(" ")[1].split(":");

                detail.setLocalDateTime(LocalDateTime.of(Integer.valueOf(sptDate[0]),
                                                         Integer.valueOf(sptDate[1]),
                                                         Integer.valueOf(sptDate[2]),
                                                         Integer.valueOf(sptTime[0]),
                                                         Integer.valueOf(sptTime[1]),
                                                         Integer.valueOf(sptTime[2])
                ));
                //Description
                colSum += dateSize;
                String colDesc = line.substring(colSum, colSum + descSize);
                detail.setSecondaryInfo(colDesc);

                //Ref
                colSum += descSize;
                String colRef = line.substring(colSum, colSum + refSize);
                detail.setReferenceNumber(colRef);

                //Amount
                colSum += refSize;
                String colAmount = line.substring(colSum, colSum + amountSize);
                detail.setAmount(BigDecimal.valueOf(Double.valueOf(colAmount)));

                //Operation
                colSum += amountSize;
                String colOperation = line.substring(colSum, colSum + operationSize);
                detail.setDebitCredit(colOperation);

                //Account code
                detail.setAccountCode(statementHeader.getAccountCode());
            }
        } else {
            detail = null;
        }

        return detail;
    }

    private void setTotalAmount(final String line, final HeaderPayload statementHeader) {
        Integer colSum = 0;

        if (line.contains("Total") && !line.equals("")) {

            //TOTAL
            colSum = 0;
            colSum += dateSize + descSize + refSize;
            String colTotal = line.substring(colSum, colSum + amountSize);

            //Banlace al monto con el que debe cerrar cada transaccion
            //detail.setBalance(BigDecimal.valueOf(Double.valueOf(colTotal)));

            statementHeader.setBalanceEnd(BigDecimal.valueOf(Double.valueOf(colTotal)));
        }
    }
}
