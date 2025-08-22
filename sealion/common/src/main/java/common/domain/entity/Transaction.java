package common.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    @JsonIgnoreProperties({"password", "contact", "role", "gender", "email","firstName", "lastName", "dob"})
    private User createdBy;

}
