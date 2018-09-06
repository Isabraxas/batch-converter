package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.Utils.FormatUtil;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
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

        return this.checkDataRowToStatemenDetail(dataRow);
    }

    public void saveStatementDetail(DetailPayload body , HeaderPayload headerPayload) {

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

        statementDetail.writePropertyDirectly("fk_header", 1200);
        //statementDetail.addToManyTarget("HeaderDetail",,true);

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

    public StatementDetail checkDataRowToStatemenDetail(DataRow dataRow) {

        StatementDetail statementDetail = new StatementDetail();

        if (dataRow != null) {
            statementDetail.setAccountCode(
                (dataRow.get("account_code") != null) ? dataRow.get("account_code").toString()
                    : statementDetail.getAccountCode());

            statementDetail.setDate((dataRow.get("date") != null) ? dataRow.get("date").toString()
                                        : statementDetail.getDate());

            statementDetail.setDebitCredit(
                (dataRow.get("debit_credit") != null) ? dataRow.get("debit_credit").toString()
                    : statementDetail.getDebitCredit());

            //TODO hacer util funcion
            Date date = (dataRow.get("local_date_time") != null) ? (Date) dataRow.get("local_date_time") : null;
            statementDetail.setLocalDateTime((date != null) ? FormatUtil.parseDateToLocalDateTime(date)
                                                 : statementDetail.getLocalDateTime());

            statementDetail.setReferenceNumber((dataRow.get("reference_number") != null) ?
                                                   dataRow.get("reference_number").toString()
                                                   : statementDetail.getReferenceNumber());

            statementDetail.setSecondaryInfo((dataRow.get("secondary_info") != null) ?
                                                 dataRow.get("secondary_info").toString()
                                                 : statementDetail.getSecondaryInfo());

            statementDetail.setTransactionCode((dataRow.get("transaction_code") != null) ?
                                                   dataRow.get("transaction_code").toString()
                                                   : statementDetail.getTransactionCode());

            statementDetail.setAnnotation((dataRow.get("annotation") != null) ? dataRow.get("annotation").toString()
                                              : statementDetail.getAnnotation());

            statementDetail.setAccountCurrency((dataRow.get("account_currency") != null) ?
                                                   dataRow.get("account_currency").toString()
                                                   : statementDetail.getAccountCurrency());

            statementDetail.setAccountType(
                (dataRow.get("account_type") != null) ? dataRow.get("account_type").toString()
                    : statementDetail.getAccountType());

            statementDetail.setAmount((dataRow.get("amount") != null) ? (BigDecimal) dataRow.get("amount")
                                          : statementDetail.getAmount());

            statementDetail.setBranchChannel((dataRow.get("branch_channel") != null) ?
                                                 dataRow.get("branch_channel").toString()
                                                 : statementDetail.getBranchChannel());

            statementDetail.setTrnId((dataRow.get("trn_id") != null) ? dataRow.get("trn_id").toString()
                                         : statementDetail.getTrnId());

            statementDetail.setBalance((dataRow.get("balance") != null) ? (BigDecimal) dataRow.get("balance")
                                           : statementDetail.getBalance());

            statementDetail.setTransactionDesc((dataRow.get("transaction_desc") != null) ?
                                                   dataRow.get("transaction_desc").toString()
                                                   : statementDetail.getTrnId());
        } else {
            statementDetail = null;
        }

        return statementDetail;
    }
}
