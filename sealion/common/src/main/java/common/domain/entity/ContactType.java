package common.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;


@Entity
@Table(
        name = "contact_types",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_contact_types_detail_created_by",
                        columnNames = {"detail", "created_by"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_contact_types_created_by",
                        columnList = "created_by"
                )
        }
)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactType extends BaseTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "detail", nullable = false)
    private String detail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "contactType", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
            name = "created_by",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_contact_types_created_by_users",
                    foreignKeyDefinition =
                            "FOREIGN KEY (created_by) REFERENCES users(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private User createdBy;


}
