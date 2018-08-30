package cc.viridian.servicebatchconverter.repository;



import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SQLExec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

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
            .query("INSERT INTO STATEMENT_HEADER(ACCOUNT_CODE,CUSTOMER_CODE,ID) VALUES (#bind($hAcc),#bind($hCtc),#bind($hId))")
            .paramsArray(body.getAccountCode(), body.getCustomerCode(), body.getId())
            .update(context);


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

        statementHeader.setAccountAddress( body.getAccountAddress());
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
