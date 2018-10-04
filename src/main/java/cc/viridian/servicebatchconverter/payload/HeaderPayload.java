package cc.viridian.servicebatchconverter.payload;

import cc.viridian.servicebatchconverter.utils.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.cayenne.DataRow;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HeaderPayload {
    private String accountAddress;
    private String accountBranch;
    private String accountCode;
    private String accountCurrency;
    private String accountName;
    private String accountType;
    private BigDecimal balanceEnd;
    private BigDecimal balanceInitial;
    private String customerCode;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String message;
    private String statementTitle;
    private String fileHash;
    private Integer id;

    public static HeaderPayload getHeaderPayload(final DataRow dataRow) {
        if (dataRow == null) {
            return null;
        }

        HeaderPayload header = new HeaderPayload();
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
            if (value != null && key.equals("id")) {
                header.setId((Integer) value);
            }
        });

        return header;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HeaderPayload that = (HeaderPayload) o;
        return Objects.equals(accountCode, that.accountCode)
            && Objects.equals(customerCode, that.customerCode)
            && Objects.equals(dateFrom, that.dateFrom)
            && Objects.equals(dateTo, that.dateTo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(accountCode, customerCode, dateFrom, dateTo);
    }
}
