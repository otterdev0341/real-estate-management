package expense.controller.internal;

import expense.domain.dto.expenseType.ReqCreateExpenseTypeDto;
import expense.domain.dto.expenseType.ReqUpdateExpenseTypeDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import java.util.UUID;

public interface InternalExpenseTypeController {

    Response createExpenseType(@Valid ReqCreateExpenseTypeDto reqCreateExpenseTypeDto);

    Response updateExpenseType(@Valid ReqUpdateExpenseTypeDto reqUpdateExpenseTypeDto, UUID expenseTypeId);

    Response deleteExpenseType(UUID expenseTypeId);

    Response findExpenseTypeById(@RequestBody(required = false) UUID expenseTypeId);

    Response findAllExpenseTypes(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, businessName, contactTypeDetail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    );

}
