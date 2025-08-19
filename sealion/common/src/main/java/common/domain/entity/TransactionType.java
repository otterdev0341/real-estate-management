package common.domain.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "transaction_types")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionType {

    @Id
    private UUID id;

    @Column(unique = true)
    private String detail;

}
