package expense.controller.internal;

import expense.domain.dto.expense.ReqCreateExpenseDto;
import expense.domain.dto.expense.ReqUpdateExpenseDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.UUID;

public interface InternalExpenseController {

    Response createExpense(@Valid ReqCreateExpenseDto reqCreateExpenseDto);

    Response updateExpense(@Valid ReqUpdateExpenseDto reqUpdateExpenseDto, UUID expenseId);

    Response deleteExpense(UUID expenseId);

    Response findExpenseById(@RequestBody(required = false) UUID expenseId);

    Response findAllExpenses(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, businessName, contactTypeDetail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    );

}
