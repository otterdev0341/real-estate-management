package expense.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Expense;
import common.errorStructure.RepositoryError;
import expense.domain.comparator.ExpenseComparators;
import expense.repository.internal.InternalExpenseRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
public class ExpenseRepositoryImpl implements PanacheRepositoryBase<Expense, UUID>, InternalExpenseRepository {

    final private JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);


    @Override
    public Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId) {
        try {
            Boolean exist = find("detail = ?1 AND createdBy.id = ?2", detail.trim(), userId).firstResultOptional().isPresent();
            return Either.right(exist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to check if detail exists" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID expenseId, UUID userId) {
        try {
            Boolean exist = find("id = ?1 and createdBy.id = ?2",expenseId, userId).firstResultOptional().isPresent();
            return Either.right(exist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to check if expenseId exists" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Expense> createExpense(Expense expense) {
        try {
            persist(expense);
            return Either.right(expense);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create expense"));
        }
    }

    @Override
    public Either<RepositoryError, Expense> updateExpense(Expense expense) {
        try {
            Expense mergedExpense = getEntityManager().merge(expense);
            getEntityManager().flush();
            return Either.right(mergedExpense);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update expense"));
        }
    }

    @Override
    public Either<RepositoryError, Expense> findExpenseAndUserId(UUID expenseId, UUID userId) {
        try {
            Optional<Expense> isExpenseExist = find("id = ?1 and createdBy.id = ?2", expenseId, userId).firstResultOptional();
            return isExpenseExist
                    .<Either<RepositoryError, Expense>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Expense not found, in repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to find expense"));
        }
    }

    @Override
    public Either<RepositoryError, List<Expense>> findAllExpenseWithUserId(UUID userId, BaseQuery query) {
        try {
            // Stream all contacts for the user
            var stream = jpaStreamer.stream(Expense.class)
                    .filter(e -> e.getCreatedBy().getId().equals(userId));

            // Apply sorting based on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<Expense> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ExpenseComparators.BY_DETAIL;
                } else if ("createdAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ExpenseComparators.BY_CREATED_AT;
                } else if ("expenseType".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ExpenseComparators.BY_EXPENSE_TYPE_DETAIL;
                } else {
                    return Either.left(new RepositoryError.FetchFailed("Invalid sortBy value: " + query.getSortBy()));
                }

                // Apply ascending or descending order
                String sortDirection = query.getSortDirection();
                if (sortDirection == null || sortDirection.isBlank() || "DESC".equalsIgnoreCase(sortDirection)) {
                    comparator = comparator.reversed(); // Default to DESC
                }

                stream = stream.sorted(comparator);
            }

            int page = query.getPage() != null ? query.getPage() : 0;
            int size = query.getSize() != null ? query.getSize() : 100;
            // Pagination logic
            int skip = page * size;
            if (skip < 0) {
                skip = 0; // Ensure skip is not negative
            }

            // Fetch the results
            List<Expense> expenses = stream.skip(skip).limit(size).toList();
            return Either.right(expenses);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error fetching expenses by user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteExpenseByIdAndUserId(UUID expenseId, UUID userId) {
        try {
            Optional<Expense> isExpenseExist = find("id = ?1 and createdBy.id = ?2", expenseId, userId).firstResultOptional();
            if (isExpenseExist.isEmpty()) {
                return Either.right(false);
            }
            delete(isExpenseExist.get());
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete expense"));
        }
    }
}
