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

    public HeaderPayload(DataRow dataRow) {
        dataRow.forEach((key, value) -> {

            if (value != null && key.equals("account_code")) {
                this.accountCode = (value.toString());
            }
            if (value != null && key.equals("date_from")) {
                Date date = (Date) value;
                if (date != null) {
                    this.dateFrom = (FormatUtil.parseDateToLocalDate(date));
                }
            }
            if (value != null && key.equals("date_to")) {
                Date date = (Date) value;
                if (date != null) {
                    this.dateTo = (FormatUtil.parseDateToLocalDate(date));
                }
            }
            if (value != null && key.equals("account_adders")) {
                this.accountAddress = (value.toString());
            }
            if (value != null && key.equals("account_branch")) {
                this.accountBranch = (value.toString());
            }
            if (value != null && key.equals("account_currency")) {
                this.accountCurrency = (value.toString());
            }
            if (value != null && key.equals("account_type")) {
                this.accountType = (value.toString());
            }
            if (value != null && key.equals("balance_end")) {
                this.balanceEnd = ((BigDecimal) value);
            }
            if (value != null && key.equals("balance_initial")) {
                this.balanceInitial = ((BigDecimal) value);
            }
            if (value != null && key.equals("customer_code")) {
                this.customerCode = (value.toString());
            }
            if (value != null && key.equals("message")) {
                this.message = (value.toString());
            }
            if (value != null && key.equals("statement_title")) {
                this.statementTitle = (value.toString());
            }
            if (value != null && key.equals("file_hash")) {
                this.fileHash = (value.toString());
            }
            if (value != null && key.equals("id")) {
                this.id = ((Integer) value);
            }
        });
    }
}
