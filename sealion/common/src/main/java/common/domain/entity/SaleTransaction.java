package common.domain.entity;

import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@Table(name = "sale_transactions")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleTransaction extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transaction", nullable = false)
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact", nullable = false)
    private Contact contact;

    @Column(name="price", nullable = false)
    private BigDecimal price;

    // relation
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "sale_transaction_file_details",
            joinColumns = @JoinColumn(name = "sale_transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<FileDetail> fileDetails = new HashSet<>();

    // relation method
    public void addFileDetail(FileDetail fileDetail) {
        if (fileDetails == null) {
            fileDetails = new HashSet<>();
        }
        this.fileDetails.add(fileDetail);
        fileDetail.getSaleTransactions().add(this);
    }

    public void removeFileDetail(FileDetail fileDetail) {
        this.getFileDetails().remove(fileDetail);
        fileDetail.getSaleTransactions().remove(this);

    }

    // implement method
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Property property = (Property) o;
        return getId() != null && Objects.equals(getId(), property.getId());
    }

}
