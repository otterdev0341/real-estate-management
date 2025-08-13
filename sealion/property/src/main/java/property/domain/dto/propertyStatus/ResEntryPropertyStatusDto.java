package property.domain.dto.propertyStatus;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResEntryPropertyStatusDto {
    private UUID id;
    private String detail;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
