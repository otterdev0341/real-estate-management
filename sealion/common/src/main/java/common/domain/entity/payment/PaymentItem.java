package common.domain.entity.payment;

import common.domain.entity.Expense;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payment_transaction_items")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "payment_transaction_id")
    private PaymentTransaction payment;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;

    private BigDecimal amount;

    private BigDecimal price;
}
