package expense.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ExpenseType;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalExpenseTypeRepository {

    Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID expenseId, UUID userId);

    Either<RepositoryError, ExpenseType> createExpenseType(ExpenseType expenseType);

    Either<RepositoryError, ExpenseType> updateExpenseType(ExpenseType expenseType);

    Either<RepositoryError, ExpenseType> findExpenseTypeAndUserId(UUID expenseTypeId, UUID userId);

    Either<RepositoryError, List<ExpenseType>> findAllExpenseTypeWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deleteExpenseTypeByIdAndUserId(UUID expenseId, UUID userId);

}
