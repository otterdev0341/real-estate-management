package memo.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ContactType;
import common.domain.entity.ExpenseType;
import common.domain.entity.MemoType;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import memo.domain.comparator.MemoTypeComparator;
import memo.repository.internal.InternalMemoTypeRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class MemoTypeRepositoryImpl implements PanacheRepositoryBase<MemoType, UUID>, InternalMemoTypeRepository {

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId) {
        try {
            Boolean isExist = find("detail = ?1 and createdBy.id = ?2", detail, userId).firstResultOptional().isPresent();
            return Either.right(isExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by detail and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID expenseTypeId, UUID userId) {
        try {
            Boolean isExist = find("id = ?1 and createdBy.id = ?2", expenseTypeId, userId).firstResultOptional().isPresent();
            return Either.right(isExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, MemoType> createMemoType(MemoType memoType) {
        try {
            persist(memoType);
            return Either.right(memoType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error creating MemoType: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, MemoType> updateMemoType(MemoType memoType) {
        try {
            MemoType mergedMemoType = getEntityManager().merge(memoType);
            getEntityManager().flush();
            return Either.right(mergedMemoType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error updating MemoType: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, MemoType> findMemoTypeAndUserId(UUID MemoTypeId, UUID userId) {
        try {
            Optional<MemoType> memoTypeOptional = find("id = ?1 and createdBy.id = ?2", MemoTypeId, userId).firstResultOptional();
            return memoTypeOptional
                    .<Either<RepositoryError, MemoType>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("MemoType not found for ID: " + MemoTypeId + " and user ID: " + userId)));
        } catch (Exception e){
            return Either.left(new RepositoryError.FetchFailed("Error fetching MemoType by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<MemoType>> findAllMemoTypeWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(MemoType.class)
                    .filter(memoType -> memoType.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<MemoType> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = MemoTypeComparator.BY_DETAIL;
                } else if ("createAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = MemoTypeComparator.BY_CREATED_AT;
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
            List<MemoType> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find memoType" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteMemoTypeByIdAndUserId(UUID MemoTypeId, UUID userId) {
        try {
            Optional<MemoType> memoTypeOptional = find("id = ?1 and createdBy.id = ?2", MemoTypeId, userId).firstResultOptional();
            if (memoTypeOptional.isPresent()) {
                delete(memoTypeOptional.get());
                return Either.right(true);
            } else {
                return Either.right(false);
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error deleting MemoType by ID and user ID: " + e.getMessage()));
        }
    }


}
