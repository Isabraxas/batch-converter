package cc.viridian.servicebatchconverter.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDate;

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
    private Long id;

}
