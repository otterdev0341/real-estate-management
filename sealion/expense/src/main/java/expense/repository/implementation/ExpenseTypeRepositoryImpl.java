package expense.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ContactType;
import common.domain.entity.ExpenseType;
import common.errorStructure.RepositoryError;
import expense.domain.comparator.ExpenseTypeComparators;
import expense.repository.internal.InternalExpenseTypeRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ExpenseTypeRepositoryImpl implements PanacheRepositoryBase<ExpenseType, UUID>, InternalExpenseTypeRepository {

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId) {
        try {
            boolean exist = find("detail = ?1 AND createdBy.id = ?2", detail.trim(), userId).firstResultOptional().isPresent();
            return Either.right(exist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to check if detail exists" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID expenseId, UUID userId) {
        try {
            boolean exist = find("id = ?1 and createdBy.id = ?2", expenseId, userId).firstResultOptional().isPresent();
            return Either.right(exist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to check if expenseId exists" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, ExpenseType> createExpenseType(ExpenseType expenseType) {
        try {
            persist(expenseType);
            return Either.right(expenseType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create expenseType reason by :" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, ExpenseType> updateExpenseType(ExpenseType expenseType) {
        try {
            ExpenseType mergedExpenseType = getEntityManager().merge(expenseType);
            return Either.right(mergedExpenseType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update expenseType reason by :" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, ExpenseType> findExpenseTypeAndUserId(UUID expenseTypeId, UUID userId) {
        try {
            Optional<ExpenseType> expenseType = find("id = ?1 and createdBy.id = ?2", expenseTypeId, userId).firstResultOptional();
            return expenseType
                    .<Either<RepositoryError, ExpenseType>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("ExpenseType not found, in repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to find expenseType reason by :" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<ExpenseType>> findAllExpenseTypeWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(ExpenseType.class)
                    .filter(expenseType -> expenseType.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<ExpenseType> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ExpenseTypeComparators.BY_DETAIL;
                } else if ("createAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ExpenseTypeComparators.BY_CREATED_AT;
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
            int size = query.getSize() != null ? query.getSize() : 10;

            // Pagination logic
            int skip = page * size;
            if (skip < 0) {
                skip = 0; // Ensure skip is not negative
            }
            if (size <= 0) {
                return Either.left(new RepositoryError.FetchFailed("Size must be greater than zero"));
            }
            List<ExpenseType> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find contactType" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteExpenseTypeByIdAndUserId(UUID expenseId, UUID userId) {
        try {
            Optional<ExpenseType> isExpenseTypeExist = find("id = ?1 and createdBy.id = ?2", expenseId, userId).firstResultOptional();
            if (isExpenseTypeExist.isEmpty()) {
                return Either.right(false);
            }
            delete(isExpenseTypeExist.get());
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete expenseType reason by :" + e.getMessage()));
        }
    }
}
