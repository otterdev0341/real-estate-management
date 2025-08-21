package payment.domain.dto.item;

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
public class ReqUpdatePaymentItemDto {

    private UUID id;

    private UUID expense;

    private BigDecimal amount;

    private BigDecimal price;
}
