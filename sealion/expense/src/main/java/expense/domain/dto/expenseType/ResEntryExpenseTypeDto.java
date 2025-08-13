package expense.domain.dto.expenseType;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ResEntryExpenseTypeDto {
    private UUID id;
    private String detail;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
