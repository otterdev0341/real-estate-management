package common.domain.entity.payment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import common.domain.entity.Expense;
import jakarta.persistence.*;
import lombok.*;

import org.hibernate.proxy.HibernateProxy;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Objects;

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
    @JsonBackReference
    private PaymentTransaction payment;

    @ManyToOne
    @JoinColumn(name = "expense_id")
    private Expense expense;

    private BigDecimal amount;

    private BigDecimal price;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PaymentItem that = (PaymentItem) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
