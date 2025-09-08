package common.domain.entity.payment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import common.domain.entity.Contact;
import common.domain.entity.FileDetail;
import common.domain.entity.Property;
import common.domain.entity.Transaction;
import common.domain.entity.base.BaseTime;
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
public class PaymentTransaction extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transaction", nullable = false)
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<PaymentItem> expenseItems = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "payment_file_details",
            joinColumns = @JoinColumn(name = "payment_transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id")
    )
    @ToString.Exclude
    @JsonManagedReference
    @Builder.Default
    private Set<FileDetail> fileDetails = new HashSet<>();


    public void addExpenseItem(PaymentItem item) {
        // กำหนดความสัมพันธ์สองทาง (bidirectional relationship)
        // ให้ item รู้จักกับ parent (PaymentTransaction)
        item.setPayment(this);
        this.expenseItems.add(item);
    }

    public void removeExpenseItem(PaymentItem item) {
        // ลบ item ออกจาก list
        this.expenseItems.remove(item);
        // ยกเลิกความสัมพันธ์
        // ทำให้ Hibernate มองเห็นว่า item นี้เป็น orphan และจะลบมันออกไป
        item.setPayment(null);
    }

    public void removeAllExpenseItems() {
        Iterator<PaymentItem> iterator = this.expenseItems.iterator();
        while (iterator.hasNext()) {
            PaymentItem item = iterator.next();
            item.setPayment(null);
            iterator.remove();
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return getId() != null && Objects.equals(getId(), that.getId());
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
