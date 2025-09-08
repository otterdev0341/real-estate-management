package investment.domain.dto.sub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryInvestmentItemDto {

    private UUID id;
    private UUID investTransaction;
    private String contact;
    private BigDecimal amount;
    private BigDecimal percent;

}
