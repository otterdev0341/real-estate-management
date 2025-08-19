package common.domain.entity.payment;


import common.domain.entity.Contact;
import common.domain.entity.FileDetail;
import common.domain.entity.Property;
import common.domain.entity.Transaction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

@Builder
@Entity
@Table(name = "payment_transactions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentItem> expenseItems = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "payment_file_details",
            joinColumns = @JoinColumn(name = "payment_transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<FileDetail> fileDetails = new HashSet<>();


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

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void addFileDetail(FileDetail fileDetail) {
        if (fileDetails == null) {
            fileDetails = new HashSet<>();
        }
        if (fileDetail.getProperties() == null) {
            fileDetail.setPaymentTransactions(new HashSet<>());
        }
        this.fileDetails.add(fileDetail);
        fileDetail.getPaymentTransactions().add(this);
    }

    public void removeFileDetail(FileDetail fileDetail) {
        this.getFileDetails().remove(fileDetail);
        fileDetail.getPaymentTransactions().remove(this);
    }
}
