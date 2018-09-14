package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.utils.FormatUtil;
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


@Slf4j
@Repository
public class StatementDetailRepository {
    private ServerRuntime mainServerRuntime;

    @Autowired
    public StatementDetailRepository(ServerRuntime mainServerRuntime) {
        this.mainServerRuntime = mainServerRuntime;
    }

    public StatementDetail getOneStatementDetail(final DetailPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Detail in DB ");
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

    public DetailPayload getLastStatementDetailPayload() {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Detail in DB ");
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM statement_detail ORDER BY id DESC LIMIT 1")
                                   .selectFirst(context);

        return this.checkDataRowToDetailPayload(dataRow);
    }

    public int deleteStatementDetailById(final Integer id) {
        log.info("Deleteing StatementDetail");
        ObjectContext context = mainServerRuntime.newContext();

        int delete = SQLExec
            .query("DELETE FROM STATEMENT_DETAIL WHERE ID = #bind($id)")
            .paramsArray(id)
            .update(context);

        return delete;
    }

    public int deleteStatementDetailByHeader(final HeaderPayload header) {
        log.info("Deleteing StatementDetail");
        ObjectContext context = mainServerRuntime.newContext();

        int delete = SQLExec
            .query("DELETE FROM STATEMENT_DETAIL WHERE ACCOUNT_CODE = #bind($accCode) AND FK_HEADER = #bind($header)")
            .paramsArray(header.getAccountCode(), header.getId())
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

    public StatementDetail checkDataRowToStatemenDetail(final DataRow dataRow) {

        StatementDetail statementDetail = new StatementDetail();
        //TODO obtener key con un stream
        if (dataRow != null) {
            StatementDetail finalStatementDetail = statementDetail;
            dataRow.forEach((k, v) -> FormatUtil.dataRowToStatementDetail(k, v, finalStatementDetail));
            statementDetail = finalStatementDetail;
        } else {
            statementDetail = null;
        }

        return statementDetail;
    }

    public DetailPayload checkDataRowToDetailPayload(final DataRow dataRow) {

        DetailPayload detailPayload = new DetailPayload();

        if (dataRow != null) {
            DetailPayload finaldetailPayload = detailPayload;
            dataRow.forEach((k, v) -> FormatUtil.dataRowToDetailPayload(k, v, finaldetailPayload));
            detailPayload = finaldetailPayload;
        } else {
            detailPayload = null;
        }

        return detailPayload;
    }
}
