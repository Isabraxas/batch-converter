package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import cc.viridian.servicebatchconverter.utils.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.query.SQLTemplate;
import org.apache.cayenne.query.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import static org.apache.cayenne.Cayenne.objectForQuery;

@Slf4j
@Repository
public class StatementHeaderRepository {
    private ServerRuntime mainServerRuntime;
    private StatementDetailRepository statementDetailRepository;

    @Autowired
    public StatementHeaderRepository(ServerRuntime mainServerRuntime,
                                     StatementDetailRepository statementDetailRepository) {
        this.mainServerRuntime = mainServerRuntime;
        this.statementDetailRepository = statementDetailRepository;
    }

    public StatementHeader getOneStatementHeaderByFileHash(final String hashCode) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Header in DB ");
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_HEADER WHERE "
                                                     + "FILE_HASH=#bind($FileHash)")
                                   .paramsArray(hashCode)
                                   .selectFirst(context);

        StatementHeader statementHeader = dataRowToStatemenHeader(dataRow);
        return statementHeader;
    }

    public StatementHeader getOneStatementHeader(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Header in DB ");

        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_HEADER WHERE "
                                                     + "ACCOUNT_CODE=#bind($AccCode)"
                                                     + " AND CUSTOMER_CODE=#bind($CustCode)"
                                                     + " AND DATE_FROM =#bind($DateFrom)"
                                                     + " AND DATE_TO =#bind($DateTo)")
                                   .paramsArray(body.getAccountCode(),
                                                body.getCustomerCode(),
                                                body.getDateFrom(),
                                                body.getDateTo()
                                   )
                                   .selectFirst(context);

        StatementHeader statementHeader = dataRowToStatemenHeader(dataRow);
        return statementHeader;
    }

    public HeaderPayload getOneStatementHeaderPayload(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Header in DB ");
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_HEADER WHERE "
                                                     + "ACCOUNT_CODE=#bind($AccCode)"
                                                     + " AND CUSTOMER_CODE=#bind($CustCode)"
                                                     + " AND DATE_FROM =#bind($DateFrom)"
                                                     + " AND DATE_TO =#bind($DateTo)")
                                   .paramsArray(
                                       body.getAccountCode(),
                                       body.getCustomerCode(),
                                       body.getDateFrom(),
                                       body.getDateTo()
                                   )
                                   .selectFirst(context);

        HeaderPayload header = this.dataRowToHeaderPayload(dataRow);

        if (header != null) {
            String sql = String.format("SELECT * FROM STATEMENT_DETAIL WHERE "
                                           + "FK_HEADER=%s", header.getId().toString());
            SQLTemplate query = new SQLTemplate(StatementDetail.class, sql);
            // ensure we are fetching DataRows
            query.setFetchingDataRows(true);
            // List of DataRow
            List<DataRow> rows = context.performQuery(query);
            List<DetailPayload> detailPayloads = new ArrayList<>();
            //Get detailPayloads
            rows.forEach(dataR -> {
                detailPayloads.add(this.statementDetailRepository.dataRowToDetailPayload(dataRow));
            });

            header.setDetailPayloads(detailPayloads);
        }

        return header;
    }

    public int deleteStatementHeaderById(final Integer id) {
        log.info("Deleteing StatementHeader");
        ObjectContext context = mainServerRuntime.newContext();

        int delete = SQLExec
            .query("DELETE FROM STATEMENT_HEADER\n"
                       + "WHERE ID = #bind($id)")
            .paramsArray(id)
            .update(context);

        return delete;
    }

    public void saveStatementHeader(final HeaderPayload body, final List<DetailPayload> detailPayloadList) {

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
        statementHeader.setFileHash(body.getFileHash());

        detailPayloadList.stream().forEach(detailPayload -> {
            StatementDetail statementDetail = new StatementDetail();
            statementDetail.setAccountCode(detailPayload.getAccountCode());
            statementDetail.setAccountCurrency(detailPayload.getAccountCurrency());
            statementDetail.setAccountType(detailPayload.getAccountType());
            statementDetail.setAmount(detailPayload.getAmount());
            statementDetail.setAnnotation(detailPayload.getAnnotation());
            statementDetail.setBalance(detailPayload.getBalance());
            statementDetail.setBranchChannel(detailPayload.getBranchChannel());
            statementDetail.setDate(FormatUtil.parseDateDBformat(detailPayload.getDate(), "-"));
            statementDetail.setDebitCredit(FormatUtil.getInitialChar(detailPayload.getDebitCredit()));
            statementDetail.setLocalDateTime(detailPayload.getLocalDateTime());
            statementDetail.setReferenceNumber(detailPayload.getReferenceNumber());
            statementDetail.setSecondaryInfo(detailPayload.getSecondaryInfo());
            statementDetail.setTransactionCode(detailPayload.getTransactionCode());
            statementDetail.setTransactionDesc(detailPayload.getTransactionDesc());

            statementHeader.addToStatementDetail(statementDetail);
        });

        context.commitChanges();
    }

    //TODO : tal vez deberia devolver un objeto con la cantidad y tipo de los registros eliminados
    public void deleteStementHeader(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        //Get Statement
        Expression qualifier = ExpressionFactory
            .matchExp(StatementHeader.ACCOUNT_CODE.getName(), body.getAccountCode())
            .andExp(ExpressionFactory.matchExp(StatementHeader.CUSTOMER_CODE.getName(), body.getCustomerCode()))
            .andExp(ExpressionFactory.matchExp(StatementHeader.DATE_FROM.getName(), body.getDateFrom()))
            .andExp(ExpressionFactory.matchExp(StatementHeader.DATE_TO.getName(), body.getDateTo()));
        SelectQuery select = new SelectQuery(StatementHeader.class, qualifier);

        //Delete Statement (header and details)
        StatementHeader header = (StatementHeader) objectForQuery(context, select);
        context.deleteObjects(header);
        context.commitChanges();
    }

    public StatementHeader dataRowToStatemenHeader(final DataRow dataRow) {
        StatementHeader statementHeader = StatementHeader.getStatementHeader(dataRow);
        return statementHeader;
    }

    public HeaderPayload dataRowToHeaderPayload(final DataRow dataRow) {
        HeaderPayload headerPayload = HeaderPayload.getHeaderPayload(dataRow);
        return headerPayload;
    }

    //TODO delete after demo
    public void deleteAllStatements() {
        log.info("Deleteing Statements");
        ObjectContext context = mainServerRuntime.newContext();

        SQLExec
            .query("DELETE FROM STATEMENT_DETAIL")
            .update(context);
        SQLExec
            .query("DELETE FROM STATEMENT_HEADER;")
            .update(context);
    }
}
