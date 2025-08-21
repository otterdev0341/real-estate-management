package payment.domain.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreatePaymentItemDto {

    private UUID id;

    private UUID expense;

    private BigDecimal amount;

    private BigDecimal price;

    public Optional<BigDecimal> getTotal() {
        if (this.amount != null && this.price != null) {
            return Optional.of(this.amount.multiply(this.price));
        }
        return Optional.empty();
    }
}
