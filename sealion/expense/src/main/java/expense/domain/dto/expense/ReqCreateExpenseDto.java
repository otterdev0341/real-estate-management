package expense.domain.dto.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateExpenseDto {
    @NotBlank(message = "Contact type cannot be blank")
    private String detail;

    @NotNull(message = "Expense type cannot be null")
    private UUID expenseType;
}
