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
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "contacts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_contacts_business_name_created_by",
                        columnNames = {"business_name", "created_by"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_contacts_created_by",
                        columnList = "created_by"
                ),
                @Index(
                        name = "idx_contacts_contact_type",
                        columnList = "contact_type"
                )
        }
)
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Contact extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "business_name", nullable = false, length = 100)
    private String businessName;

    @Column(name = "internal_name", length = 100)
    private String internalName;

    @Column(name = "detail", length = 255)
    private String detail;

    @Column(name = "note", length = 255)
    private String note;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"createdBy", "contact", "createdAt", "updatedAt"})
    @JoinColumn(
            name = "contact_type",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_contacts_contact_type",
                    foreignKeyDefinition =
                            "FOREIGN KEY (contact_type) REFERENCES contact_types(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    private ContactType contactType;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;

    @Column(name = "line", length = 50)
    private String line;

    @Column(name = "email", length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "contact", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
            name = "created_by",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_contacts_created_by",
                    foreignKeyDefinition =
                            "FOREIGN KEY (created_by) REFERENCES users(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private User createdBy;

}
