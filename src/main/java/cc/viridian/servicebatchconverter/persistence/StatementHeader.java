package cc.viridian.servicebatchconverter.persistence;

import cc.viridian.servicebatchconverter.persistence.auto._StatementHeader;
import cc.viridian.servicebatchconverter.utils.FormatUtil;
import org.apache.cayenne.DataRow;
import java.math.BigDecimal;
import java.util.Date;

public class StatementHeader extends _StatementHeader {

    private static final long serialVersionUID = 1L;

    public StatementHeader() {

    }

    public static StatementHeader getStatementHeader(final DataRow dataRow) {
        if (dataRow == null) {
            return null;
        }

        StatementHeader header = new StatementHeader();
        dataRow.forEach((key, value) -> {

            if (value != null && key.equals("account_code")) {
                header.setAccountCode(value.toString());
            }
            if (value != null && key.equals("date_from")) {
                Date date = (Date) value;
                if (date != null) {
                    header.setDateFrom(FormatUtil.parseDateToLocalDate(date));
                }
            }
            if (value != null && key.equals("date_to")) {
                Date date = (Date) value;
                if (date != null) {
                    header.setDateTo(FormatUtil.parseDateToLocalDate(date));
                }
            }
            if (value != null && key.equals("account_adders")) {
                header.setAccountAddress(value.toString());
            }
            if (value != null && key.equals("account_branch")) {
                header.setAccountBranch(value.toString());
            }
            if (value != null && key.equals("account_currency")) {
                header.setAccountCurrency(value.toString());
            }
            if (value != null && key.equals("account_type")) {
                header.setAccountType(value.toString());
            }
            if (value != null && key.equals("balance_end")) {
                header.setBalanceEnd((BigDecimal) value);
            }
            if (value != null && key.equals("balance_initial")) {
                header.setBalanceInitial((BigDecimal) value);
            }
            if (value != null && key.equals("customer_code")) {
                header.setCustomerCode(value.toString());
            }
            if (value != null && key.equals("message")) {
                header.setMessage(value.toString());
            }
            if (value != null && key.equals("statement_title")) {
                header.setStatementTitle(value.toString());
            }
            if (value != null && key.equals("file_hash")) {
                header.setFileHash(value.toString());
            }
        });
        return header;
    }

    /*
    public static StatementHeader getStatementHeader(final HeaderPayload headerPayload) {
        if (headerPayload == null) {
            return null;
        }

        StatementHeader header = new StatementHeader();
        if (headerPayload.getAccountCode() != null ) {
            header.setAccountCode(headerPayload.getAccountCode());
        }
        if (headerPayload.getDateFrom() != null) {
            header.setDateFrom(headerPayload.getDateFrom());
        }
        if (headerPayload.getDateFrom() != null) {
            header.setDateFrom(headerPayload.getDateFrom());
        }
        if (headerPayload != null && key.equals("account_adders")) {
            header.setAccountAddress(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("account_branch")) {
            header.setAccountBranch(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("account_currency")) {
            header.setAccountCurrency(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("account_type")) {
            header.setAccountType(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("balance_end")) {
            header.setBalanceEnd((BigDecimal) headerPayload);
        }
        if (headerPayload != null && key.equals("balance_initial")) {
            header.setBalanceInitial((BigDecimal) headerPayload);
        }
        if (headerPayload != null && key.equals("customer_code")) {
            header.setCustomerCode(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("message")) {
            header.setMessage(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("statement_title")) {
            header.setStatementTitle(headerPayload.toString());
        }
        if (headerPayload != null && key.equals("file_hash")) {
            header.setFileHash(headerPayload.toString());
        }
        return header;
    }
    */
}
