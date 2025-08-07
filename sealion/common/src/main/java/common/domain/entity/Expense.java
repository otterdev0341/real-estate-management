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
        name = "expenses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_expenses_detail_created_by_expense_type",
                        columnNames = {"detail", "created_by", "expense_type"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_expenses_created_by",
                        columnList = "created_by"
                ),
                @Index(
                        name = "idx_expenses_expense_type",
                        columnList = "expense_type"
                )
        }
)
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Expense extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "detail", nullable = false, length = 255)
    private String detail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"createdBy", "expense", "createdAt", "updatedAt"})
    @JoinColumn(
            name = "expense_type",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_expenses_expense_type",
                    foreignKeyDefinition =
                            "FOREIGN KEY (expense_type) REFERENCES expense_types(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private ExpenseType expenseType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "expense", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
            name = "created_by",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_expenses_created_by",
                    foreignKeyDefinition =
                            "FOREIGN KEY (created_by) REFERENCES users(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private User createdBy;
}
