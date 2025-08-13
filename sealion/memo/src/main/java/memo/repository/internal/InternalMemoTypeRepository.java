package memo.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.MemoType;
import common.errorStructure.RepositoryError;
import java.util.List;
import java.util.UUID;


public interface InternalMemoTypeRepository {
    Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID memoTypeId, UUID userId);

    Either<RepositoryError, MemoType> createMemoType(MemoType memoType);

    Either<RepositoryError, MemoType> updateMemoType(MemoType memoType);

    Either<RepositoryError, MemoType> findMemoTypeAndUserId(UUID MemoTypeId, UUID userId);

    Either<RepositoryError, List<MemoType>> findAllMemoTypeWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deleteMemoTypeByIdAndUserId(UUID MemoTypeId, UUID userId);
}
