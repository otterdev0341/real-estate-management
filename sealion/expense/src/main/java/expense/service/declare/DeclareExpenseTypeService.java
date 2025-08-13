package expense.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.ExpenseType;
import common.errorStructure.ServiceError;

import java.util.UUID;

public interface DeclareExpenseTypeService {

    Either<ServiceError, Boolean> isExpenseTypeExistWithUserId(UUID expenseTypeId, UUID userId);

    Either<ServiceError, ExpenseType> findExpenseTypeByIdAndUserId(UUID expenseTypeId, UUID userId);

}
