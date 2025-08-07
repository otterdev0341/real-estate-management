package common.domain.entity;

import java.math.BigDecimal;
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
@Data
@EqualsAndHashCode(callSuper = true)
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
    private Contact ownerBy;

    @Column(name = "map_url", length = 255)
    private String mapUrl;

    @Column(name = "lat", length = 50)
    private String lat;

    @Column(name = "lng", length = 50)
    private String lng;

    @Column(name = "sold", nullable = false)
    private Boolean sold = false;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private User createdBy;
    
    //relation
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_property_types",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "property_type_id"),
            foreignKey = @ForeignKey(name = "fk_property_property_type_property"),
            inverseForeignKey = @ForeignKey(name = "fk_property_property_type_type")
    )
    @JsonIgnoreProperties("properties") // Prevent circular references
    private Set<PropertyType> propertyTypes = new HashSet<>();


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_file_details",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "file_detail_id"),
            foreignKey = @ForeignKey(name = "fk_property_file_property"),
            inverseForeignKey = @ForeignKey(name = "fk_property_file_file_detail")
    )
    private Set<FileDetail> fileDetails = new HashSet<>();

    @ManyToMany(mappedBy = "properties", fetch = FetchType.LAZY)
    @ToString.Exclude
    @JsonIgnoreProperties("properties") // Prevent circular reference
    private Set<Memo> memos = new HashSet<>();

    // method


}
