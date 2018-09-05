package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.Utils.FormatUtil;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
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
import java.util.Date;

@Slf4j
@Repository
public class StatementDetailRepository {
    private ServerRuntime mainServerRuntime;

    @Autowired
    public StatementDetailRepository(ServerRuntime mainServerRuntime) {
        this.mainServerRuntime = mainServerRuntime;
    }

    public StatementDetail getOneStatementDetail(DetailPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Header in DB ");
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_DETAIL WHERE "
                                                     + "ACCOUNT_CODE=#bind($AccCode)"
                                                     + " AND DEBIT_CREDIT=#bind($DebitCredit)"
                                         //            + " AND LOCAL_DATE_TIME =#bind($DateTime)"
                                                     + " AND REFERENCE_NUMBER =#bind($RefNum)")
                                   .paramsArray(body.getAccountCode()
                                       , FormatUtil.getInitialChar(body.getDebitCredit())
                                       //, body.getLocalDateTime()
                                       , body.getReferenceNumber())
                                   .selectFirst(context);
        StatementDetail statementDetail = new StatementDetail();

        try {

            statementDetail.setAccountCode(dataRow.get("account_code").toString());
            statementDetail.setDate(dataRow.get("date").toString());
            statementDetail.setDebitCredit(dataRow.get("debit_credit").toString());

            //TODO hacer util funcion
            Date date= (Date) dataRow.get("local_date_time");
            Instant instant = Instant.ofEpochMilli(date.getTime());
            LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            statementDetail.setLocalDateTime(localDateTime);

            statementDetail.setReferenceNumber(dataRow.get("reference_number").toString());
            statementDetail.setSecondaryInfo(dataRow.get("secondary_info").toString());


        } catch (NullPointerException nullp) {
            statementDetail = null;
            log.error(nullp.getMessage());

        }

        return statementDetail;
    }

    public void saveStatementDetail(DetailPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Saving new Detail in DB ");
        StatementDetail statementDetail = context.newObject(StatementDetail.class);
        //Reformat
        body = this.reformatDetail(body);

        statementDetail.setAccountCode(body.getAccountCode());
        statementDetail.setAccountCurrency(body.getAccountCurrency());
        statementDetail.setAccountType(body.getAccountType());
        statementDetail.setAmount(body.getAmount());
        statementDetail.setAnnotation(body.getAnnotation());
        statementDetail.setBalance(body.getBalance());
        statementDetail.setBranchChannel(body.getBranchChannel());
        statementDetail.setDate(body.getDate());
        statementDetail.setDebitCredit(body.getDebitCredit());
        statementDetail.setLocalDateTime(body.getLocalDateTime());
        statementDetail.setReferenceNumber(body.getReferenceNumber());
        statementDetail.setSecondaryInfo(body.getSecondaryInfo());
        statementDetail.setTransactionCode(body.getTransactionCode());
        statementDetail.setTransactionDesc(body.getTransactionDesc());

        context.commitChanges();
    }

    public int deleteStatementDetail(DetailPayload body) {
        log.info("Deleteing StatementDetail");
        ObjectContext context = mainServerRuntime.newContext();

        int delete = SQLExec
            .query("DELETE FROM STATEMENT_DETAIL\n" +
                       "WHERE ACCOUNT_CODE = #bind($accCode)")
            .paramsArray(body.getAccountCode())
            .update(context);

        return delete;
    }

    private DetailPayload reformatDetail(final DetailPayload body) {
        DetailPayload detailPayload = body;

        //Date 8 chars
        String newDate = FormatUtil.parseDateDBformat(detailPayload.getDate(), "-");
        detailPayload.setDate(newDate);
        //Debit-Credit
        String newOp = FormatUtil.getInitialChar(detailPayload.getDebitCredit());
        detailPayload.setDebitCredit(newOp);

        return detailPayload;
    }
}
