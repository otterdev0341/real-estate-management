package expense.domain.dto.expense;

import lombok.Data;

import java.util.UUID;

@Data
public class ResEntryExpenseDto {
    private UUID id;
    private String expense;
    private String expenseType;
    private String createdBy;
}
