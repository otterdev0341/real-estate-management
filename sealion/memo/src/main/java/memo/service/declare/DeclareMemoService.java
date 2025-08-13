package memo.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.Memo;
import common.domain.entity.MemoType;
import common.errorStructure.ServiceError;

import java.util.UUID;

public interface DeclareMemoService {
    Either<ServiceError, Boolean> isExistByIdAndUserId(UUID memoId, UUID userId);

    Either<ServiceError, Memo> findMemoByIdAndUserId(UUID memoId, UUID userId);
}
