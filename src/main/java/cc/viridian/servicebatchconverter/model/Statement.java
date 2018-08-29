package cc.viridian.servicebatchconverter.model;

import cc.viridian.servicebatchconverter.payload.DetailPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Statement {
    private StatementHeader header;
    private List<DetailPayload> details;


    @Override
    public String toString() {

        StringBuffer sb = new StringBuffer();

        if (header != null) {
            sb.append("account: " + header.getAccountCode());
            sb.append(" " + header.getAccountCurrency());
            sb.append(" " + header.getAccountType());
            sb.append(" " + header.getAccountName());
            sb.append(" " + header.getBalanceEnd());
        }

        if (details != null) {
            sb.append(" transactions: " + details.size());
        } else {
            sb.append(" transactions: 0");
        }

        return sb.toString();
    }
}
