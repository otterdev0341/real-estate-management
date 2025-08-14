package common.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.domain.entity.base.BaseTime;
import common.service.declare.fileAssetManagement.HasFileDetails;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(
        name = "memos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_memos_name_created_by",
                        columnNames = {"name", "created_by"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_memos_created_by",
                        columnList = "created_by"
                ),
                @Index(
                        name = "idx_memos_memo_type",
                        columnList = "memo_type"
                )
        }
)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Memo extends BaseTime implements HasFileDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "detail", length = 255)
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "memo_type",
            referencedColumnName = "id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_memos_memo_type",
                    foreignKeyDefinition =
                            "FOREIGN KEY (memo_type) REFERENCES memo_types(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private MemoType memoType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "contact", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
            name = "created_by",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_memos_created_by",
                    foreignKeyDefinition =
                            "FOREIGN KEY (created_by) REFERENCES users(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private User createdBy;

    // relation
    @ManyToMany
    @JoinTable(
            name = "memo_file_detail",
            joinColumns = @JoinColumn(name = "memo_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_memo_file_detail",
                    columnNames = {"memo_id", "file_detail_id"}
            )
    )
    @ToString.Exclude
    @Builder.Default
    private Set<FileDetail> fileDetails = new HashSet<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "memo_property",
            joinColumns = @JoinColumn(name = "memo_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "property_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uq_memo_property",
                    columnNames = {"memo_id", "property_id"}
            )
    )
    @ToString.Exclude
    @JsonIgnoreProperties("memos") // Prevent circular reference
    private Set<Property> properties = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Memo memo = (Memo) o;
        return getId() != null && Objects.equals(getId(), memo.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    // relation method
    public void addFileDetail(FileDetail fileDetail) {
        this.getFileDetails().add(fileDetail);
        fileDetail.getMemos().add(this);
    }

    public void removeFileDetail(FileDetail fileDetail) {
        this.getFileDetails().remove(fileDetail);
        fileDetail.getMemos().remove(this);
    }

}
