package expense.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Expense;
import common.domain.entity.ExpenseType;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalExpenseRepository {

    Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID expenseId, UUID userId);

    Either<RepositoryError, Expense> createExpense(Expense expense);

    Either<RepositoryError, Expense> updateExpense(Expense expense);

    Either<RepositoryError, Expense> findExpenseAndUserId(UUID expenseId, UUID userId);

    Either<RepositoryError, List<Expense>> findAllExpenseWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deleteExpenseByIdAndUserId(UUID expenseId, UUID userId);

}
