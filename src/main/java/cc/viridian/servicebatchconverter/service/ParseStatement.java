package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.payload.StatementPayload;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ParseStatement {

    public List<StatementPayload> parseContent(String filePath) throws FileNotFoundException, IOException {
        FileReader f = new FileReader(filePath);
        BufferedReader b = new BufferedReader(f);

        String line;
        String[] splitLine;
        Integer currentLine = 0;

        StatementPayload statement = new StatementPayload();
        List<DetailPayload> detailList = new ArrayList<DetailPayload>();
        List<StatementPayload> statementPayloadList = new ArrayList<StatementPayload>();
        Integer i=0;
        Boolean startDetailsLine= false;
        Integer colSum=0;

        Integer dateSize = null;
        Integer descSize = null;
        Integer refSize = null;
        Integer amountSize = null;
        Integer operationSize = null;

        HeaderPayload statementHeader = new HeaderPayload();

        while((line = b.readLine())!=null) {

            //statement = new StatementPayload();

            DetailPayload detail = new DetailPayload();

            if (!line.contains("-----------------")) {

                currentLine++;
                System.out.println(line);

                //splitLine = line.split(",");
                //System.out.println("SPLIT: "+splitLine);
                //System.out.println("SPLIT: " + Arrays.toString(splitLine));


                    if (line.contains("Bank")) {

                        splitLine = line.split(": ");
                        statementHeader.setAccountBranch(splitLine[1]);
                        
                    }
                    if (line.contains("Address")) {

                        splitLine = line.split(": ");
                        statementHeader.setAccountAddress(splitLine[1]);
                        
                    }
                    if (line.contains("Statement")) {

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

                        
                    }
                    if (line.contains("Customer")) {

                        splitLine = line.split(": ");
                        statementHeader.setCustomerCode(splitLine[1]);
                        
                    }
                    if (line.contains("Account")) {

                        splitLine = line.split(": ");
                        statementHeader.setAccountCode(splitLine[1]);
                        
                    }




                if (line.contains("Date:")) {
                    dateSize= 20;
                    descSize= 90;
                    refSize= 20;
                    amountSize= 20;
                    operationSize= 15;

                    //startDetailsLine= currentLine+1;
                    startDetailsLine= true;
                }

                if(startDetailsLine == true && !line.contains("Total") && !line.contains("Date:")){

                    if(!line.equals("")){
                        colSum=0;

                        //Date
                        String colDate=line.substring(colSum, colSum + dateSize);
                        detail.setDate(colDate.split(" ")[0]);
                        //LocalDateTime
                        String[] sptDate= colDate.split(" ")[0].split("-");
                        String[] sptTime= colDate.split(" ")[1].split(":");

                        detail.setLocalDateTime(LocalDateTime.of(Integer.valueOf(sptDate[0]),
                                                                 Integer.valueOf(sptDate[1]),
                                                                 Integer.valueOf(sptDate[2]),
                                                                 Integer.valueOf(sptTime[0]),
                                                                 Integer.valueOf(sptTime[1]),
                                                                 Integer.valueOf(sptTime[2])));
                        //Description
                        colSum += dateSize;
                        String colDesc=line.substring(colSum, colSum + descSize);
                        detail.setSecondaryInfo(colDesc);

                        //Ref
                        colSum += descSize;
                        String colRef=line.substring(colSum, colSum + refSize);
                        detail.setReferenceNumber(colRef);

                        //Amount
                        colSum += refSize;
                        String colAmount=line.substring(colSum, colSum + amountSize);
                        detail.setAmount(BigDecimal.valueOf(Double.valueOf(colAmount)));

                        //Operation
                        colSum += amountSize;
                        String colOperation=line.substring(colSum, colSum + operationSize);
                        detail.setDebitCredit(colOperation);

                        //Accoont code
                        detail.setAccountCode(statementHeader.getAccountCode());

                        detailList.add(detail);
                    }else {
                        startDetailsLine = false;
                    }


                }

                if(startDetailsLine == false && line.contains("Total") && !line.equals("")){
                     
                    //TOTAL
                    colSum = 0;
                    colSum += dateSize + descSize + refSize;
                    String colTotal=line.substring(colSum, colSum + amountSize);
                    //Banlace al monte de cerrar cada transaccion
                    //detail.setBalance(BigDecimal.valueOf(Double.valueOf(colTotal)));

                    statementHeader.setBalanceEnd(BigDecimal.valueOf(Double.valueOf(colTotal)));

                }

            }

            //startDetailsLine = false;
            statement.setHeader(statementHeader);
            System.out.println("HEADER: "+statementHeader);
            statement.setDetails(detailList);
            System.out.println("DETAILS: "+detailList);

            if(line.contains("-----------------")) {

                statementPayloadList.add(statement);

                statement = new StatementPayload();
                statementHeader = new HeaderPayload();
                detailList = new ArrayList<DetailPayload>();
            }
        }
        b.close();

        return statementPayloadList;
    }



}
