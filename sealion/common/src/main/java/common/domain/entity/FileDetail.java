package common.domain.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;


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
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class FileDetail extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

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
    private FileType type;

    @Column(name = "size", nullable = false)
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
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
    private Set<Property> properties = new HashSet<>();
    // method

}
