package common.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import jakarta.persistence.CascadeType;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(
    name = "properties",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_properties_name_created_by",
            columnNames = {"name", "created_by"}
        )
    },
    indexes = {
        @Index(
            name = "idx_properties_created_by",
            columnList = "created_by"
        ),
        @Index(
            name = "idx_properties_status",
            columnList = "status"
        ),
        @Index(
            name = "idx_properties_owner_by",
            columnList = "owner_by"
        )
    }
)
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Property extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "specific", length = 255)
    private String specific;

    @Column(name = "highlight", length = 50)
    private String highlight;

    @Column(name = "area", length = 50)
    private String area;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "f_s_p", precision = 10, scale = 2)
    private BigDecimal fsp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "status",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_properties_status",
            foreignKeyDefinition = 
                "FOREIGN KEY (status) REFERENCES property_statuses(id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE"
        )
    )
    @JsonBackReference
    @ToString.Exclude
    private PropertyStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_by",
        referencedColumnName = "id",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "fk_properties_owner",
            foreignKeyDefinition = 
                "FOREIGN KEY (owner_by) REFERENCES contacts(id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE"
        )
    )
    @JsonBackReference
    @ToString.Exclude
    private Contact ownerBy;

    @Column(name = "map_url", length = 255)
    private String mapUrl;

    @Column(name = "lat", length = 50)
    private String lat;

    @Column(name = "lng", length = 50)
    private String lng;

    @Column(name = "sold", nullable = false)
    private Boolean sold = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties({"password", "contact", "role", "gender", "email","firstName", "lastName", "dob"})
    @JoinColumn(
        name = "created_by",
        referencedColumnName = "id",
        foreignKey = @ForeignKey(
            name = "fk_properties_created_by",
            foreignKeyDefinition = 
                "FOREIGN KEY (created_by) REFERENCES users(id) " +
                "ON DELETE RESTRICT ON UPDATE CASCADE"
        )
    )
    @JsonBackReference
    @ToString.Exclude
    private User createdBy;
    
    //relation
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "property_property_types",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "property_type_id"),
            foreignKey = @ForeignKey(name = "fk_property_property_type_property"),
            inverseForeignKey = @ForeignKey(name = "fk_property_property_type_type")
    )
    @JsonIgnoreProperties("properties")
    @ToString.Exclude // Prevent circular references
    private Set<PropertyType> propertyTypes = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "property_file_details",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<FileDetail> fileDetails = new HashSet<>();

    @ManyToMany(mappedBy = "properties", fetch = FetchType.EAGER)
    @ToString.Exclude
    @JsonIgnoreProperties("properties") // Prevent circular reference
    private Set<Memo> memos = new HashSet<>();



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

    // relation method
    // file detail
    public void addFileDetail(FileDetail fileDetail) {
        if (fileDetails == null) {
            fileDetails = new HashSet<>();
        }
        if (fileDetail.getProperties() == null) {
            fileDetail.setProperties(new HashSet<>());
        }
        this.fileDetails.add(fileDetail);
        fileDetail.getProperties().add(this);
    }

    public void removeFileDetail(FileDetail fileDetail) {
        this.getFileDetails().remove(fileDetail);
        fileDetail.getProperties().remove(this);
    }
    // property type
    public void addPropertyType(PropertyType propertyType) {
        if(!this.propertyTypes.contains(propertyType)) {
            this.getPropertyTypes().add(propertyType);
            propertyType.getProperties().add(this);
        }
    }

    public void removePropertyType(PropertyType propertyType) {
        this.getPropertyTypes().remove(propertyType);
        propertyType.getProperties().remove(this);
    }

    public void addMemo(Memo memo) {
        if(!this.memos.contains(memo)) {
            this.getMemos().add(memo);
            memo.getProperties().add(this);
        }
    }

    public void removeMemo(Memo memo) {
        this.getMemos().remove(memo);
        memo.getProperties().remove(this);
    }

}
