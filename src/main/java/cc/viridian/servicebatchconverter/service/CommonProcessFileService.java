package cc.viridian.servicebatchconverter.service;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.utils.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class CommonProcessFileService {

    static Integer dateSize = null;
    static Integer descSize = null;
    static Integer amountSize = null;
    static Integer tempBalanceSize = null;
    static Integer operationSize = null;

    public static HeaderPayload fillStatementAccountHeader(final String line, final HeaderPayload headerPayload) {
        log.debug("Starting to fill statement header");
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

        log.debug("Ending to fill statement header");
        return statementHeader;
    }

    public static Boolean setSizeColumnsOfStatementAccountDetailHeader(final String line,
        final Boolean startReadDetails) {
        Boolean rStartReadDetails;
        log.debug("Starting to set size columns vars");
        if (line.contains("Date:")) {
            dateSize = 20;
            descSize = 90;
            amountSize = 20;
            tempBalanceSize = 20;
            operationSize = 15;

            rStartReadDetails = true;
        } else {
            rStartReadDetails = startReadDetails;
        }

        log.debug("Ending to set size columns vars");
        return rStartReadDetails;
    }

    public static DetailPayload fillStatementDetailAccountRecord(final String line,
                                                                 final DetailPayload detailPayload,
                                                                 final HeaderPayload headerPayload)
        throws StringIndexOutOfBoundsException {
        log.debug("Starting to fill one statement detail");
        DetailPayload detail = detailPayload;
        HeaderPayload statementHeader = headerPayload;
        Integer colSum = 0;
        Integer res = 0;

        if (!line.contains("Total") && !line.contains("Date:")) {

            if (!line.equals("")) {
                colSum = 0;

                //Date
                res = colSum + dateSize;
                String colDate = line.substring(colSum, res);
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
                res = colSum + descSize;
                String colDesc = line.substring(colSum, res);
                detail.setSecondaryInfo(colDesc);

                //Amount
                colSum += descSize;
                res = colSum + amountSize;
                String colAmount = line.substring(colSum, res);
                detail.setAmount(BigDecimal.valueOf(Double.valueOf(colAmount)));

                //Balance of detail
                colSum += amountSize;
                res = colSum + tempBalanceSize;
                String colBalance = line.substring(colSum, res);
                detail.setBalance(BigDecimal.valueOf(Double.valueOf(colBalance)));

                //Operation
                colSum += tempBalanceSize;
                res = colSum + operationSize;
                if (line.contains("DEBITO")) {
                    res = colSum + "DEBITO".length();
                } else {
                    res = colSum + "CREDITO".length();
                }
                String colOperation = line.substring(colSum, res);
                detail.setDebitCredit(colOperation);

                //Account code
                detail.setAccountCode(statementHeader.getAccountCode());
            }
        } else {
            detail = null;
        }
        log.debug("Ending to fill one statement detail");
        return detail;
    }

    public static void setTotalAmount(final String line, final HeaderPayload statementHeader) {
        log.debug("Starting to fill total amount");
        Integer colSum = 0;

        if (line.contains("Total") && !line.equals("")) {

            //TOTAL
            colSum = 0;
            colSum += dateSize + descSize + amountSize;
            String colTotal = line.substring(colSum, colSum + tempBalanceSize);

            log.debug("TOTAL= " + Double.valueOf(colTotal));
            statementHeader.setBalanceEnd(BigDecimal.valueOf(Double.valueOf(colTotal)));
        }
        log.debug("Ending to fill total amount");
    }
}
