package cc.viridian.servicebatchconverter.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StatementPayload {

    private HeaderPayload header;
    private List<DetailPayload> details;

    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (this.header != null) {
            sb.append("account: " + this.header.getAccountCode());
            sb.append(" " + this.header.getAccountCurrency());
            sb.append(" " + this.header.getAccountType());
            sb.append(" " + this.header.getAccountName());
            sb.append(" " + this.header.getBalanceEnd());
        }

        if (this.details != null) {
            sb.append(" transactions: " + this.details.size());
        } else {
            sb.append(" transactions: 0");
        }

        return sb.toString();
    }

}
