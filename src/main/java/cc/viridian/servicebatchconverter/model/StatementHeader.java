package cc.viridian.servicebatchconverter.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatementHeader {

    private String accountAddress;
    private String accountBranch;
    private String accountCode;
    private String accountCurrency;
    private String accountName;
    private String accountType;
    private BigDecimal balanceEnd;
    private BigDecimal balanceInitial;
    private String CustomerCode;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String message;
    private String statementTitle;

      @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();

        sb.append("StatementHeader{");
        sb.append("accountAddress='" + accountAddress + '\'');
        sb.append(", accountBranch='" + accountBranch + '\'');
        sb.append(", accountCode='" + accountCode + '\'');
        sb.append(", accountCurrency='" + accountCurrency + '\'');
        sb.append(", accountName='" + accountName + '\'');
        sb.append(", accountType='" + accountType + '\'');
        sb.append(", balanceEnd=" + balanceEnd);
        sb.append(", balanceInitial=" + balanceInitial);
        sb.append(", CustomerCode='" + CustomerCode + '\'');
        sb.append(", dateFrom=" + dateFrom);
        sb.append(", dateTo=" + dateTo);
        sb.append(", message='" + message + '\'');
        sb.append(", statementTitle='" + statementTitle + '\'');
        sb.append("}");

        return sb.toString();
    }
}
