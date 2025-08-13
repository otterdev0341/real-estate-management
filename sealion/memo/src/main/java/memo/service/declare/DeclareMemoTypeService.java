package memo.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.MemoType;
import common.errorStructure.ServiceError;

import java.util.UUID;

public interface DeclareMemoTypeService {

    Either<ServiceError, Boolean> isExistByIdAndUserId(UUID memoTypeId, UUID userId);

    Either<ServiceError, MemoType> findMemoTypeByIdAndUserId(UUID memoTypeId, UUID userId);

}
