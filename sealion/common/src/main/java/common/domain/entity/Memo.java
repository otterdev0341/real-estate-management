package common.domain.entity;

import java.util.HashSet;
import java.util.Set;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


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
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class Memo extends BaseTime {
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

    @ManyToOne(fetch = FetchType.LAZY)
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
}
