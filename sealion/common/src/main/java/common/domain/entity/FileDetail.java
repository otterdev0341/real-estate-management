package common.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(
    name = "file_details",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_file_details_name_created_by",
            columnNames = {"name", "created_by", "type"}
        )
    },
    indexes = {
        @Index(
            name = "idx_file_details_created_by",
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
public class FileDetail extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;


    @JsonIgnore
    @Column(name = "object_key", nullable = false, length = 255)
    private String objectKey;

    @Column(name = "path", nullable = false, length = 255)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "type",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_file_details_type",
            foreignKeyDefinition = 
                "FOREIGN KEY (type) REFERENCES file_types(id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE"
        )
    )
    @JsonBackReference
    @ToString.Exclude
    private FileType type;

    @JsonIgnore
    @Column(name = "size", nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "contactType", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
        name = "created_by",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "fk_file_details_created_by",
            foreignKeyDefinition = 
                "FOREIGN KEY (created_by) REFERENCES users(id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE"
        )
    )
    @JsonBackReference
    @ToString.Exclude
    private User createdBy;

    // relation
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_file_details",
            joinColumns = @JoinColumn(name = "file_detail_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id"),
            foreignKey = @ForeignKey(name = "fk_property_file_file_detail"),
            inverseForeignKey = @ForeignKey(name = "fk_property_file_property")
    )
    @ToString.Exclude
    @JsonBackReference
    private Set<Property> properties = new HashSet<>();

    @ManyToMany(mappedBy = "fileDetails")
    @JsonIgnoreProperties("fileDetails")
    @ToString.Exclude
    @Builder.Default
    @JsonBackReference
    private Set<Memo> memos = new HashSet<>();


    // method

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        FileDetail that = (FileDetail) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
