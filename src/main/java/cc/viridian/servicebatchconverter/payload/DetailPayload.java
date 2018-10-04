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

    public static DetailPayload getDetailPayload(final DataRow dataRow) {
        if (dataRow == null) {
            return null;
        }
        DetailPayload detail = new DetailPayload();
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
            if (value != null && key.equals("id")) {
                detail.setId((Integer) value);
            }
        });
        return detail;
    }
}
