package cc.viridian.servicebatchconverter.repository;

import cc.viridian.servicebatchconverter.utils.FormatUtil;
import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.payload.HeaderPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import cc.viridian.servicebatchconverter.persistence.StatementHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.cayenne.DataRow;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.query.SQLExec;
import org.apache.cayenne.query.SQLSelect;
import org.apache.cayenne.query.SQLTemplate;
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
    public StatementHeaderRepository(ServerRuntime mainServerRuntime
        , StatementDetailRepository statementDetailRepository) {
        this.mainServerRuntime = mainServerRuntime;
        this.statementDetailRepository = statementDetailRepository;
    }

    public void registerStatementHeader(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        log.info("Saving new Header in DB ");
        int insert = SQLExec
            .query(
                "INSERT INTO STATEMENT_HEADER(ACCOUNT_CODE,CUSTOMER_CODE,ID)" +
                    " VALUES (#bind($hAcc),#bind($hCtc),#bind($hId))")
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
            //log.error(nullp.getMessage());
            //nullp.printStackTrace();
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
                                   .paramsArray(body.getAccountCode()
                                       , body.getCustomerCode()
                                       , body.getDateFrom()
                                       , body.getDateTo())
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
                                   .paramsArray(body.getAccountCode()
                                       , body.getCustomerCode()
                                       , body.getDateFrom()
                                       , body.getDateTo())
                                   .selectFirst(context);

        return this.checkDataRowToHeaderPayload(dataRow);
    }

    public int deleteStatementHeaderById(final Integer id) {
        log.info("Deleteing StatementHeader");
        ObjectContext context = mainServerRuntime.newContext();

        int delete = SQLExec
            .query("DELETE FROM STATEMENT_HEADER\n" +
                       "WHERE ID = #bind($id)")
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

    //TODO tal vez deberia devolver un objeto con la cantidad y tipo de los registros elininados
    public void deleteStementHeader(final HeaderPayload body) {

        ObjectContext context = mainServerRuntime.newContext();

        //TODO obtener el objeto si es posible el header y los detail
        HeaderPayload header = this.getOneStatementHeaderPayload(body);

        //TODO obtener details en base a la fk_header
        String sql = "SELECT D.* FROM STATEMENT_HEADER H LEFT JOIN STATEMENT_DETAIL D ON H.ID = D.FK_HEADER"
            + " WHERE H.ACCOUNT_CODE=#bind($AccCode)"
            + " AND H.CUSTOMER_CODE=#bind($CustCode)"
            + " AND H.DATE_FROM =#bind($DateFrom)"
            + " AND H.DATE_TO =#bind($DateTo)";
        SQLTemplate selectQuery = new SQLTemplate(StatementHeader.class, sql);
        selectQuery.setParamsArray(body.getAccountCode()
            , body.getCustomerCode()
            , body.getDateFrom()
            , body.getDateTo());

        // ensure we are fetching DataRows
        selectQuery.setFetchingDataRows(true);

        List<DataRow> rows = context.performQuery(selectQuery);
        ///Call funcion of detail repository qby convert datarow to payload

        //TODO eliminar details
        this.statementDetailRepository.deleteStatementDetailByHeader(header);
        //rows.stream().forEach(dataRow -> {
          //  this.statementDetailRepository.deleteStatementDetailById((Integer) dataRow.get("id"));
        //});

        //TODO eliminar header
        this.deleteStatementHeaderById(header.getId());
    }

    public StatementHeader checkDataRowToStatemenHeader(final DataRow dataRow) {

        if (dataRow != null) {
            StatementHeader statementHeader = StatementHeader.getStatementHeader(dataRow);
            if (statementHeader != null) {
                return statementHeader;
            } else {
                return null;
            }
        } else {
            return null;
        }

    }

    public HeaderPayload checkDataRowToHeaderPayload(final DataRow dataRow) {

        if (dataRow != null) {
            HeaderPayload headerPayload = new HeaderPayload(dataRow);
            if (headerPayload != null) {
                return headerPayload;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
