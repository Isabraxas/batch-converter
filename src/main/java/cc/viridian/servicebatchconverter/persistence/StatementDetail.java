package cc.viridian.servicebatchconverter.persistence;

import cc.viridian.servicebatchconverter.persistence.auto._StatementDetail;
import cc.viridian.servicebatchconverter.utils.FormatUtil;
import org.apache.cayenne.DataRow;
import java.math.BigDecimal;
import java.util.Date;

public class StatementDetail extends _StatementDetail {

    private static final long serialVersionUID = 1L;

    // this method allow to access Foreign keys
    public Long getStatementHeaderId() {
        StatementHeader statementHeader = getStatementHeader();
        if (statementHeader != null) {
            return (Long) statementHeader.getObjectId().getIdSnapshot().get(StatementHeader.ID_PK_COLUMN);
        } else {
            return null;
        }
    }

    public StatementDetail() {
    }

    public static StatementDetail getStatementDetail(final DataRow dataRow) {
        if (dataRow == null) {
            return null;
        }

        StatementDetail detail = new StatementDetail();
        dataRow.forEach((key, value) -> {

            if (value != null && key.equals("account_code")) {
                detail.setAccountCode(value.toString());
            }
            if (value != null && key.equals("date")) {
                detail.setDate(value.toString());
            }
            if (value != null && key.equals("debit_credit")) {
                detail.setDebitCredit(value.toString());
            }
            if (value != null && key.equals("local_date_time")) {
                Date date = (Date) value;
                if (date != null) {
                    detail.setLocalDateTime(FormatUtil.parseDateToLocalDateTime(date));
                }
            }
            if (value != null && key.equals("reference_number")) {
                detail.setReferenceNumber(value.toString());
            }
            if (value != null && key.equals("secondary_info")) {
                detail.setSecondaryInfo(value.toString());
            }
            if (value != null && key.equals("transaction_code")) {
                detail.setTransactionCode(value.toString());
            }
            if (value != null && key.equals("annotation")) {
                detail.setAnnotation(value.toString());
            }
            if (value != null && key.equals("account_currency")) {
                detail.setAccountCurrency(value.toString());
            }
            if (value != null && key.equals("account_type")) {
                detail.setAccountType(value.toString());
            }
            if (value != null && key.equals("amount")) {
                detail.setAmount((BigDecimal) value);
            }
            if (value != null && key.equals("branch_channel")) {
                detail.setBranchChannel(value.toString());
            }
            if (value != null && key.equals("trn_id")) {
                detail.setTrnId(value.toString());
            }
            if (value != null && key.equals("balance")) {
                detail.setBalance((BigDecimal) value);
            }
            if (value != null && key.equals("transaction_desc")) {
                detail.setTransactionDesc(value.toString());
            }
        });

        return detail;
    }
}
