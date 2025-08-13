package expense.domain.dto.expenseType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqCreateExpenseTypeDto {

    @NotBlank(message = "Contact type cannot be blank")
    private String detail;

}
