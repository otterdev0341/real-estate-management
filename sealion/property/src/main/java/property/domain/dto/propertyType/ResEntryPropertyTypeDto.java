package property.domain.dto.propertyType;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
public class ResEntryPropertyTypeDto {

    private UUID id;
    private String detail;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
