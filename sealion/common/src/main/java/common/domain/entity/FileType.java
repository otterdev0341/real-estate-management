package common.domain.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "file_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileType {
    @Id
    private UUID id;


    @Column(name = "detail", unique = true, nullable = false)
    private String detail;
}
