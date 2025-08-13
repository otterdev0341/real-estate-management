package expense.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.Expense;
import common.errorStructure.ServiceError;

import java.util.UUID;

public interface DeclareExpenseService {

    Either<ServiceError, Boolean> isExpenseExistWithUserId(UUID expenseId, UUID userId);

    Either<ServiceError, Expense> findExpenseByIdAndUserId(UUID expenseId, UUID userId);

}
