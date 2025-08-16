package property.domain.dto.propertyStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryPropertyStatusDto {
    private UUID id;
    private String detail;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
