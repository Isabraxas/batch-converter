package cc.viridian.servicebatchconverter.payload;

import cc.viridian.servicebatchconverter.utils.FormatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.cayenne.DataRow;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DetailPayload {

    public String accountCode;
    public String accountCurrency;
    public String accountType;
    public BigDecimal amount;
    public String annotation;
    public BigDecimal balance;
    public String branchChannel;
    public String date;
    public String debitCredit;
    public LocalDateTime localDateTime;
    public String referenceNumber;
    public String secondaryInfo;
    public String transactionCode;
    public String transactionDesc;
    public String trnId;
    public Integer id;

    public DetailPayload(DataRow dataRow) {
        dataRow.forEach((key, value) -> {

            if (value != null && key.equals("account_code")) {
                this.accountCode = (value.toString());
            }
            if (value != null && key.equals("date")) {
                this.setDate(value.toString());
            }
            if (value != null && key.equals("debit_credit")) {
                this.debitCredit = (value.toString());
            }
            if (value != null && key.equals("local_date_time")) {
                Date date = (Date) value;
                if (date != null) {
                    this.localDateTime = (FormatUtil.parseDateToLocalDateTime(date));
                }
            }
            if (value != null && key.equals("reference_number")) {
                this.referenceNumber = (value.toString());
            }
            if (value != null && key.equals("secondary_info")) {
                this.setSecondaryInfo(value.toString());
            }
            if (value != null && key.equals("transaction_code")) {
                this.transactionCode = (value.toString());
            }
            if (value != null && key.equals("annotation")) {
                this.annotation = (value.toString());
            }
            if (value != null && key.equals("account_currency")) {
                this.accountCurrency = (value.toString());
            }
            if (value != null && key.equals("account_type")) {
                this.accountType = (value.toString());
            }
            if (value != null && key.equals("amount")) {
                this.amount = ((BigDecimal) value);
            }
            if (value != null && key.equals("branch_channel")) {
                this.branchChannel = (value.toString());
            }
            if (value != null && key.equals("trn_id")) {
                this.trnId = (value.toString());
            }
            if (value != null && key.equals("balance")) {
                this.balance = ((BigDecimal) value);
            }
            if (value != null && key.equals("transaction_desc")) {
                this.transactionDesc = (value.toString());
            }
            if (value != null && key.equals("id")) {
                this.id = ((Integer) value);
            }
        });
    }
}
