package common.domain.entity.investment;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import common.domain.entity.FileDetail;
import common.domain.entity.Property;
import common.domain.entity.Transaction;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

@Entity
@Table(name = "invest_transactions")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvestmentTransaction extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "transaction", nullable = false)
    @JsonIgnoreProperties("invest_transactions")
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    private List<InvestmentItem> investmentItems = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "investment_file_details",
            joinColumns = @JoinColumn(name = "investment_transaction_id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id")
    )
    @ToString.Exclude
    @JsonManagedReference
    @Builder.Default
    private Set<FileDetail> fileDetails = new HashSet<>();

    public void addInvestmentItem(InvestmentItem item) {
        item.setInvestment(this);
        this.investmentItems.add(item);
    }

    public void removeInvestmentItem(InvestmentItem item) {
        this.investmentItems.remove(item);
        item.setInvestment(null);
    }

    public void removeAllInvestmentItems() {
        Iterator<InvestmentItem> iterator = this.investmentItems.iterator();
        while (iterator.hasNext()) {
            InvestmentItem item = iterator.next();
            item.setInvestment(null);
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
        InvestmentTransaction that = (InvestmentTransaction) o;
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
        fileDetail.getInvestmentTransactions().add(this);
    }

    public void removeFileDetail(FileDetail fileDetail) {
        this.getFileDetails().remove(fileDetail);
        fileDetail.getInvestmentTransactions().remove(this);
    }

}
