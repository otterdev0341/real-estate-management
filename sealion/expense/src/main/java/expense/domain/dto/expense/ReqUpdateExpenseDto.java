package expense.domain.dto.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ReqUpdateExpenseDto {
    @NotBlank(message = "Contact type cannot be blank")
    private String detail;

    @NotNull(message = "Expense type cannot be null")
    private UUID expenseType;
}
