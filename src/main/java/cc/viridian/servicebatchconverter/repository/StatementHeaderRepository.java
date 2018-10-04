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
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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

    public void registerStatementHeader(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Saving new Header in DB ");
        int insert = SQLExec
            .query(
                "INSERT INTO STATEMENT_HEADER(ACCOUNT_CODE,CUSTOMER_CODE,ID)"
                + " VALUES (#bind($hAcc),#bind($hCtc),#bind($hId))")
            .paramsArray(body.getAccountCode(), body.getCustomerCode(), body.getId())
            .update(context);
    }

    public StatementHeader getOneStatementHeaderByFileHash(final String hashCode) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Select Header in DB ");
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_HEADER WHERE "
                                                     + "FILE_HASH=#bind($FileHash)")
                                   .paramsArray(hashCode)
                                   .selectFirst(context);
        StatementHeader statementHeader = new StatementHeader();

        try {

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
            statementHeader.setFileHash(dataRow.get("file_hash").toString());
        } catch (NullPointerException nullp) {
            statementHeader = null;
        }

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
                                       body.getDateTo())
                                   .selectFirst(context);

        return this.checkDataRowToStatemenHeader(dataRow);
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
                                       body.getDateTo())
                                   .selectFirst(context);

        return this.checkDataRowToHeaderPayload(dataRow);
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

    public void saveStatementHeader(final HeaderPayload body) {

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

    //TODO: tal vez deberia devolver un objeto con la cantidad y tipo de los registros eliminados
    public void deleteStementHeader(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        //Get header
        HeaderPayload header = this.getOneStatementHeaderPayload(body);

        //Delete details
        this.statementDetailRepository.deleteStatementDetailByHeader(header);

        //Delete header
        this.deleteStatementHeaderById(header.getId());
    }

    public StatementHeader checkDataRowToStatemenHeader(final DataRow dataRow) {

        if (dataRow != null) {
            StatementHeader statementHeader = StatementHeader.getStatementHeader(dataRow);
            return statementHeader;
        } else {
            return null;
        }
    }

    public HeaderPayload checkDataRowToHeaderPayload(final DataRow dataRow) {

        if (dataRow != null) {
            HeaderPayload headerPayload = HeaderPayload.getHeaderPayload(dataRow);
            return headerPayload;
        } else {
            return null;
        }
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
