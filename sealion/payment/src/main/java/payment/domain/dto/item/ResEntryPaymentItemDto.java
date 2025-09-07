package payment.domain.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryPaymentItemDto {
    private UUID id;
    private UUID paymentTransaction;
    private String expense;
    private BigDecimal amount;
    private BigDecimal price;

}
