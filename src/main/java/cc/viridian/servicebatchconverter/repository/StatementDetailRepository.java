package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.Utils.FormatUtil;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.DataObject;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SQLExec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class StatementDetailRepository {
    private ServerRuntime mainServerRuntime;

    @Autowired
    public StatementDetailRepository(ServerRuntime mainServerRuntime) {
        this.mainServerRuntime = mainServerRuntime;
    }

    public void saveStatementDetail(DetailPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Saving new Header in DB ");
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
