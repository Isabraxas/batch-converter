package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.Utils.FormatUtil;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
@Repository
public class StatementHeaderRepository {
    private ServerRuntime mainServerRuntime;

    @Autowired
    public StatementHeaderRepository(ServerRuntime mainServerRuntime) {
        this.mainServerRuntime = mainServerRuntime;
    }

    public void registerStatementHeader(HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Saving new Header in DB ");
        int insert = SQLExec
            .query(
                "INSERT INTO STATEMENT_HEADER(ACCOUNT_CODE,CUSTOMER_CODE,ID) VALUES (#bind($hAcc),#bind($hCtc),#bind($hId))")
            .paramsArray(body.getAccountCode(), body.getCustomerCode(), body.getId())
            .update(context);
    }

    public StatementHeader getOneStatementHeader(HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Header in DB ");
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_HEADER WHERE "
                                                     + "ACCOUNT_CODE=#bind($AccCode)"
                                                     + " AND CUSTOMER_CODE=#bind($CustCode)"
                                                     + " AND DATE_FROM =#bind($DateFrom)"
                                                     + " AND DATE_TO =#bind($DateTo)")
                                   .paramsArray(body.getAccountCode()
                                       , body.getCustomerCode()
                                       , body.getDateFrom()
                                       , body.getDateTo())
                                   .selectFirst(context);
        StatementHeader statementHeader = new StatementHeader();

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            statementHeader.setAccountAddress(dataRow.get("account_address").toString());
            statementHeader.setAccountBranch(dataRow.get("account_branch").toString());
            statementHeader.setAccountCode(dataRow.get("account_code").toString());
            statementHeader.setCustomerCode(dataRow.get("customer_code").toString());
            Date dateFrom = (Date) dataRow.get("date_from");
            LocalDate localDateFrom = FormatUtil.parseDateToLocalDate(dateFrom);
            statementHeader.setDateFrom(localDateFrom);
            Date dateTo = (Date) dataRow.get("date_to");
            LocalDate localDateTo = FormatUtil.parseDateToLocalDate(dateTo);
            statementHeader.setDateTo(localDateTo);
            statementHeader.setStatementTitle(dataRow.get("statement_title").toString());

        }catch (NullPointerException nullp){
            statementHeader = null;
            log.error(nullp.getMessage());
            //nullp.printStackTrace();
        }

        return statementHeader;
    }

    public int deleteByCustomer(HeaderPayload body) {
        log.info("Deleteing StatementHeader");
        ObjectContext context = mainServerRuntime.newContext();

        int delete = SQLExec
            .query("DELETE FROM STATEMENT_HEADER\n" +
                       "WHERE CUSTOMER_CODE = #bind($cusCode)")
            .paramsArray(body.getCustomerCode())
            .update(context);

        return delete;
    }

    public void saveStatementHeader(HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Saving new Header in DB ");
        StatementHeader statementHeader = context.newObject(StatementHeader.class);

        statementHeader.setAccountAddress(body.getAccountAddress());
        statementHeader.setAccountBranch(body.getAccountBranch());
        statementHeader.setAccountCode(body.getAccountCode());
        statementHeader.setAccountCurrency(body.getAccountCurrency());
        statementHeader.setAccountType(body.getAccountType());
        statementHeader.setBalanceEnd(body.getBalanceEnd());
        statementHeader.setBalanceInitial(body.getBalanceInitial());
        statementHeader.setCustomerCode(body.getCustomerCode());
        statementHeader.setDateFrom(body.getDateFrom());
        statementHeader.setDateTo(body.getDateTo());
        statementHeader.setMessage(body.getMessage());
        statementHeader.setStatementTitle(body.getStatementTitle());

        context.commitChanges();
    }
}
