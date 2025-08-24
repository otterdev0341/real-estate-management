package investment.domain.dto.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvestmentItemDto {

    private UUID id;

    private UUID contact;

    private BigDecimal amount;

    private BigDecimal percent;

}
