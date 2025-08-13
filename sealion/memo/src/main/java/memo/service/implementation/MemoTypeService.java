package memo.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.MemoType;
import common.errorStructure.ServiceError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import memo.domain.dto.memoType.ReqCreateMemoTypeDto;
import memo.domain.dto.memoType.ReqUpdateMemoTypeDto;
import memo.repository.internal.InternalMemoRepository;
import memo.repository.internal.InternalMemoTypeRepository;
import memo.service.declare.DeclareMemoTypeService;
import memo.service.internal.InternalMemoTypeService;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.UUID;

@ApplicationScoped
public class MemoTypeService implements DeclareMemoTypeService, InternalMemoTypeService {

    private final InternalMemoTypeRepository memoTypeRepository;
    private final DeclareUserService userService;

    @Inject
    public MemoTypeService(InternalMemoTypeRepository memoTypeRepository, DeclareUserService userService) {
        this.memoTypeRepository = memoTypeRepository;
        this.userService = userService;
    }

    @Override
    public Either<ServiceError, Boolean> isExistByIdAndUserId(UUID memoTypeId, UUID userId) {
        return memoTypeRepository.isExistByIdAndUserId(memoTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Fail to check is memo type exist or not :" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, MemoType> findMemoTypeByIdAndUserId(UUID memoTypeId, UUID userId) {
        return memoTypeRepository.findMemoTypeAndUserId(memoTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to find memo type by id, cause by :" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }


    @Override
    public Either<ServiceError, MemoType> createNewMemoTypeType(ReqCreateMemoTypeDto reqCreateMemoTypeDto, UUID userId) {
        // get user object
        // check is new memo detail not exist
        // prepare memo type
        // persist
        return userService.findUserById(userId)
                .flatMap(
                        Either::left,
                        user -> {
                            return memoTypeRepository.isExistByDetailAndUserId(reqCreateMemoTypeDto.getDetail().trim(), userId)
                                    .mapRight(isExist -> Pair.of(user, isExist))
                                    .mapLeft(error -> new ServiceError.OperationFailed("Failed to check is new memo type exist" + error.message()));
                        }
                ).flatMapRight(pair -> {
                    if (pair.getRight()) {
                        ServiceError theError = new ServiceError.DuplicateEntry("the memo type name already exist");
                        return Either.left(theError);
                    }
                    MemoType memoType = MemoType.builder()
                            .detail(reqCreateMemoTypeDto.getDetail().trim())
                            .createdBy(pair.getLeft())
                            .build();
                    return memoTypeRepository.createMemoType(memoType)
                            .flatMap(
                                    error -> {
                                        ServiceError theError = new ServiceError.PersistenceFailed("Fail to create memo type causes by" + error.message());
                                        return Either.left(theError);
                                    },
                                    Either::right
                            );
                });
    }

    @Override
    public Either<ServiceError, MemoType> updateMemoTypeType(ReqUpdateMemoTypeDto reqUpdateMemoTypeDto, UUID userId, UUID memoTypeId) {
        // check is memo type exist
        // check is updated detail exist
        // prepare payload
        // persist
        return memoTypeRepository.findMemoTypeAndUserId(memoTypeId, userId)
                .flatMap(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to find memo type" + error.message());
                            return Either.left(theError);
                        },
                        foundedMemoType -> {
                            return memoTypeRepository.isExistByDetailAndUserId(reqUpdateMemoTypeDto.getDetail().trim(), userId)
                                    .mapRight(isExist -> Pair.of(foundedMemoType, isExist))
                                    .mapLeft(error -> new ServiceError.ValidationFailed("Failed to check if updated detail of memo type exists, cause by: " + error.message()));
                        }
                )
                // flatMapRight is used here to continue the chain with the Pair
                .flatMap(
                        Either::left, // Re-wrap the Left value
                        pair -> {
                            if (pair.getRight()) {
                                ServiceError theError = new ServiceError.DuplicateEntry("the new memo type name already exists");
                                return Either.left(theError);
                            }
                            MemoType memoType = (MemoType) pair.getLeft();
                            if (memoType.getDetail().trim().equalsIgnoreCase(reqUpdateMemoTypeDto.getDetail().trim())) {
                                ServiceError theError = new ServiceError.ValidationFailed("the new memo type name is the same as currently name");
                                return Either.left(theError);
                            }
                            memoType.setDetail(reqUpdateMemoTypeDto.getDetail().trim());

                            // The return type here must be Either<ServiceError, MemoType>
                            return memoTypeRepository.updateMemoType(memoType)
                                    .mapLeft(error -> new ServiceError.PersistenceFailed("Fail to update memo type cause by: " + error.message()));
                        }
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteMemoTypeTypeByIdAndUserId(UUID memoTypeTypeId, UUID userId) {
        return memoTypeRepository.deleteMemoTypeByIdAndUserId(memoTypeTypeId, userId)
                .fold(
                        error -> {
                            ServiceError serviceError = new ServiceError.OperationFailed("Failed to delete memo type reason by: " + error.message());
                            return Either.left(serviceError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<MemoType>> findAllMemoTypeTypeWithUserId(UUID userId, BaseQuery query) {
        return memoTypeRepository.findAllMemoTypeWithUserId(userId, query)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to fetch all memo type cause by:" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }
}
