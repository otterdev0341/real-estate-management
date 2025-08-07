package common.domain.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import common.domain.entity.base.BaseTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "memo_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_memo_types_detail_created_by",
                        columnNames = {"detail", "created_by"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_memo_types_created_by",
                        columnList = "created_by"
                )
        }
)
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoType extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "detail", nullable = false, length = 50)
    private String detail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "memoTypes", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
            name = "created_by",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_memo_types_created_by",
                    foreignKeyDefinition =
                            "FOREIGN KEY (created_by) REFERENCES users(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private User createdBy;
}
