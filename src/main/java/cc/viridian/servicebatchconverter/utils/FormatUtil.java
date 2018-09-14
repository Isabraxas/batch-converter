package cc.viridian.servicebatchconverter.utils;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import cc.viridian.servicebatchconverter.persistence.StatementDetail;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class FormatUtil {
    public static String parseDateDBformat(final String date, final String separator) {
        String rDate = null;
        String[] sptdate = date.split(separator);
        rDate = sptdate[0] + sptdate[1] + sptdate[2];
        return rDate;
    }

    public static String getInitialChar(final String s) {
        String rS = s.substring(0, 1);
        return rS;
    }

    public static String parseToNull(final String s) {
        String rS = s.contains("null") ? null : s;
        return rS;
    }

    public static LocalDate parseDateToLocalDate(final Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
        return localDate;
    }

    public static LocalDateTime parseDateToLocalDateTime(final Date date) {
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return localDateTime;
    }

    public static StatementDetail dataRowToStatementDetail(final String key, final Object value
        , final StatementDetail statementDetail) {
        StatementDetail detail = statementDetail;

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
                detail.setLocalDateTime(parseDateToLocalDateTime(date));
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

        return detail;
    }

    public static DetailPayload dataRowToDetailPayload(final String key, final Object value
        , final DetailPayload detailPayload) {
        DetailPayload detail = detailPayload;

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
                detail.setLocalDateTime(parseDateToLocalDateTime(date));
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
        if (value != null && key.equals("id")) {
            detail.setId((Integer) value);
        }

        return detail;
    }

}
