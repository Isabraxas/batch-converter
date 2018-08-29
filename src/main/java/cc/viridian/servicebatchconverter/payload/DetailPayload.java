package cc.viridian.servicebatchconverter.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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

}
