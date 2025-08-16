package memo.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.Memo;
import common.errorStructure.RepositoryError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import memo.domain.comparator.MemoComparator;
import memo.repository.internal.InternalMemoRepository;

import java.util.*;
import java.util.stream.Stream;

@ApplicationScoped
@Named("memoRepository")
public class MemoRepositoryImpl implements PanacheRepositoryBase<Memo, UUID>, InternalMemoRepository, FileAssetManagementRepository {

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);


    @Override
    public Either<RepositoryError, Boolean> isExistByNameAndUserId(String memoName, UUID userId) {
        try {
            Boolean isExist = find("name = ?1 and createdBy.id = ?2", memoName.trim(), userId).firstResultOptional().isPresent();
            return Either.right(isExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by name and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID MemoId, UUID userId) {
        try {
            Boolean isExist = find("id = ?1 and createdBy.id = ?2", MemoId, userId).firstResultOptional().isPresent();
            return Either.right(isExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Memo> createMemo(Memo memo) {
        try {
            persist(memo);
            return Either.right(memo);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create memo: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Memo> updateMemo(Memo memo) {
        try {
            Memo mergedMemo = getEntityManager().merge(memo);
            getEntityManager().flush();
            return Either.right(mergedMemo);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update memo: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Memo> findMemoAndUserId(UUID memoId, UUID userId) {
        try {
            Optional<Memo> contactType = find("id = ?1 and createdBy.id = ?2", memoId, userId).firstResultOptional();
            return contactType.<Either<RepositoryError, Memo>>map(Either::right).orElseGet(() -> Either.left(new RepositoryError.NotFound("Memo not found in due repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Operation failed on finding Memo in repository"));
        }
    }

    @Override
    public Either<RepositoryError, List<Memo>> findAllMemoWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(Memo.class)
                    .filter(memo -> memo.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<Memo> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = MemoComparator.BY_DETAIL;
                } else if ("createAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = MemoComparator.BY_CREATED_AT;
                } else if ("memoType".equalsIgnoreCase(query.getSortBy())) {
                    comparator = MemoComparator.BY_MEMO_TYPE;
                }
                else {
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
            List<Memo> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find contactType" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteMemoByIdAndUserId(UUID MemoId, UUID userId) {
        try {
            Optional<Memo> memo = find("id = ?1 and createdBy.id = ?2", MemoId, userId).firstResultOptional();
            if (memo.isPresent()) {
                delete(memo.get());
                return Either.right(true);
            } else {
                return Either.right(false);
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete memo: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        try {
            // Find the specific memo first
            Either<RepositoryError, Memo> memoExist = findMemoAndUserId(targetId, userId);
            if (memoExist.isLeft()) {
                return Either.left(new RepositoryError.NotFound("Memo not found"));
            }
            Memo memo = memoExist.getRight();

            // Get the files from the memo's fileDetails collection
            Set<FileDetail> fileDetails = memo.getFileDetails();

            // Dynamically filter the files based on the criteria
            Stream<FileDetail> fileStream = fileDetails.stream();

            if (fileCase.equals(FileCaseSelect.ALL)) {
                // No additional filtering needed
                return Either.right(fileStream.toList());
            }

            List<FileDetail> result = fileStream
                    .filter(fileDetail -> FileCaseSelect.fileCaseMatches(fileDetail, fileCase))
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch file related of memo: " + e.getMessage()));
        }
    }
}
