package memo.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.MemoType;
import common.errorStructure.ServiceError;
import memo.domain.dto.memoType.ReqCreateMemoTypeDto;
import memo.domain.dto.memoType.ReqUpdateMemoTypeDto;


import java.util.List;
import java.util.UUID;

public interface InternalMemoTypeService {

    Either<ServiceError, MemoType> createNewMemoTypeType(ReqCreateMemoTypeDto reqCreateMemoTypeDto, UUID userId);
    
    Either<ServiceError, MemoType> updateMemoTypeType(ReqUpdateMemoTypeDto reqUpdateMemoTypeDto, UUID userId, UUID memoTypeId);
    
    Either<ServiceError, Boolean> deleteMemoTypeTypeByIdAndUserId(UUID memoTypeTypeId, UUID userId);
    
    Either<ServiceError, List<MemoType>> findAllMemoTypeTypeWithUserId(UUID userId, BaseQuery query);
}
