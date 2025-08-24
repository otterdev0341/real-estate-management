package common.domain.entity.investment;

import common.domain.entity.Contact;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "invest_transaction_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvestmentItem extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "invest_transaction_id")
    private InvestmentTransaction investment;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    private BigDecimal amount;

    private BigDecimal percent;

}
