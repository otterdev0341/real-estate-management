package memo.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Memo;
import common.errorStructure.ServiceError;
import memo.domain.dto.memo.ReqCreateMemoDto;
import memo.domain.dto.memo.ReqUpdateMemoDto;


import java.util.List;
import java.util.UUID;

public interface InternalMemoService {
    Either<ServiceError, Boolean> isExistByNameAndUserId(String name, UUID userId);

    Either<ServiceError, Memo> createNewMemo(ReqCreateMemoDto reqCreateMemo, UUID userId);

    Either<ServiceError, Memo> updateMemo(ReqUpdateMemoDto reqUpdateMemoDto, UUID userId, UUID memoId);

    Either<ServiceError, Boolean> deleteMemoByIdAndUserId(UUID MemoId, UUID userId);

    Either<ServiceError, List<Memo>> findAllMemoWithUserId(UUID userId, BaseQuery query);
}
