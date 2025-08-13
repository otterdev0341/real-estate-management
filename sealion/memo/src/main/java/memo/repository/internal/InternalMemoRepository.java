package memo.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Memo;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalMemoRepository {

    Either<RepositoryError, Boolean> isExistByNameAndUserId(String memoName, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID MemoId, UUID userId);

    Either<RepositoryError, Memo> createMemo(Memo memo);

    Either<RepositoryError, Memo> updateMemo(Memo memo);

    Either<RepositoryError, Memo> findMemoAndUserId(UUID memoId, UUID userId);

    Either<RepositoryError, List<Memo>> findAllMemoWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deleteMemoByIdAndUserId(UUID MemoId, UUID userId);
}
