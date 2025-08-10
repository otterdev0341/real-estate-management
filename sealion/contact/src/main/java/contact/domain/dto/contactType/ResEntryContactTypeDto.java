package contact.domain.dto.contactType;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;


@Data
public class ResEntryContactTypeDto {
    private UUID id;
    private String detail;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
