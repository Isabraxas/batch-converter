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
        //TODO review query
        DataRow dataRow = SQLSelect.dataRowQuery("SELECT * FROM STATEMENT_DETAIL WHERE "
                                                     + "ACCOUNT_CODE=#bind($AccCode)"
                                                     + " AND DEBIT_CREDIT=#bind($DebitCredit)"
                                                     + " AND LOCAL_DATE_TIME =#bind($DateTime)")
                                   .paramsArray(body.getAccountCode(),
                                                FormatUtil.getInitialChar(body.getDebitCredit()),
                                                body.getLocalDateTime()
                                   )
                                   .selectFirst(context);

        StatementDetail statementDetail = StatementDetail.getStatementDetail(dataRow);
        return statementDetail;
    }

}
