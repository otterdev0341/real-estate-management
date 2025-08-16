package property.service.implementation;


import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyStatus;
import common.domain.entity.User;
import common.errorStructure.ServiceError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import property.domain.dto.propertyStatus.ReqCreatePropertyStatusDto;
import property.domain.dto.propertyStatus.ReqUpdatePropertyStatusDto;
import property.repository.internal.InternalPropertyStatusRepository;
import property.service.declare.DeclarePropertyStatusService;
import property.service.internal.InternalPropertyStatusService;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PropertyStatusService implements DeclarePropertyStatusService, InternalPropertyStatusService {

    private final DeclareUserService userService;
    private final InternalPropertyStatusRepository propertyStatusRepository;

    @Inject
    public PropertyStatusService(DeclareUserService userService, InternalPropertyStatusRepository propertyStatusRepository) {
        this.userService = userService;
        this.propertyStatusRepository = propertyStatusRepository;
    }

    @Override
    public Either<ServiceError, Boolean> isPropertyStatusExistByIdAndUserId(UUID propertyStatusId, UUID userId) {
        return propertyStatusRepository.isExistByIdAndUserId(propertyStatusId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to check is property status exist or not, causes by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, PropertyStatus> findPropertyStatusByIdAndUserId(UUID propertyStatusId, UUID userId) {
        return propertyStatusRepository.findPropertyStatusByIdAndUserId(propertyStatusId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error occurred by finding property status by id, cause by:" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, PropertyStatus> createNewPropertyStatus(ReqCreatePropertyStatusDto reqCreatePropertyStatusDto, UUID userId) {
        // check is user exist
        // check is new detail exist
        // prepare payload
        // persist
        return userService.findUserById(userId)
                .flatMap(
                        Either::left,
                        user -> {
                            return propertyStatusRepository.isExistByDetailAndUserId(reqCreatePropertyStatusDto.getDetail().trim(), userId)
                                    .mapRight(exist -> Pair.of(user, exist))
                                    .mapLeft(error -> new ServiceError.OperationFailed("Error occurred while checking new property status detail exist, cause by:" + error.message()));
                        }
                ).flatMapRight(pair -> {
                    User user = pair.getLeft();
                    boolean isNewPropertyStatusDetailExist = pair.getRight();
                    if (isNewPropertyStatusDetailExist) {
                        ServiceError theError = new ServiceError.DuplicateEntry("the property status is already exist");
                        return Either.left(theError);
                    }
                    PropertyStatus propertyStatus = PropertyStatus.builder()
                            .detail(reqCreatePropertyStatusDto.getDetail().trim())
                            .createdBy(user)
                            .build();
                    return propertyStatusRepository.createPropertyStatus(propertyStatus)
                            .flatMapRight(Either::right)
                            .mapLeft(theError -> new ServiceError.OperationFailed("Error occurred while create new property status, cause by:" + theError.message()));
                });
    }

    @Override
    public Either<ServiceError, PropertyStatus> updatePropertyStatus(ReqUpdatePropertyStatusDto reqUpdatePropertyStatusDto, UUID propertyStatusId, UUID userId) {
        // check is property status exist
        // check is new name to update exist
        // prepare payload
        // persist
        return propertyStatusRepository.findPropertyStatusByIdAndUserId(propertyStatusId, userId)
                .mapLeft(error -> (ServiceError) new ServiceError.OperationFailed("Error occurred while fetching property status by id, cause by: " + error.message()))
                .flatMapRight(
                        founded -> {
                            return propertyStatusRepository.isExistByDetailAndUserId(reqUpdatePropertyStatusDto.getDetail().trim(), userId)
                                    .mapRight(exist -> Pair.of(founded, exist))
                                    .mapLeft(error -> new ServiceError.OperationFailed("Error occurred while checking new property status detail to update, cause by: " + error.message()));
                        }
                ).flatMapRight(pair -> {
                    PropertyStatus targetPropertyStatus = pair.getLeft();
                    boolean isUpdatedPropertyStatusDetailExist = pair.getRight();
                    if (isUpdatedPropertyStatusDetailExist) {
                        ServiceError theError = new ServiceError.DuplicateEntry("property status to update already exist");
                        return Either.left(theError);
                    }
                    if (targetPropertyStatus.getDetail().trim().equals(reqUpdatePropertyStatusDto.getDetail().trim())) {
                        ServiceError theError = new ServiceError.DuplicateEntry("property status to update is the same as current value");
                        return Either.left(theError);
                    }
                    targetPropertyStatus.setDetail(reqUpdatePropertyStatusDto.getDetail().trim());
                    return propertyStatusRepository.updatePropertyStatus(targetPropertyStatus)
                            .mapRight(success -> success)
                            .mapLeft(theError -> new ServiceError.OperationFailed("Error occurred while updating property status, cause by: " + theError.message()));
                });
    }

    @Override
    public Either<ServiceError, Boolean> deletePropertyStatus(UUID propertyStatusId, UUID userId) {
        return propertyStatusRepository.deletePropertyStatusByIdAndUserId(propertyStatusId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error occurred on deleting property status, cause by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<PropertyStatus>> findAllPropertyStatues(UUID userId, BaseQuery query) {
        return propertyStatusRepository.findAllPropertyStatusWithUserId(userId, query)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error occurred on fetching property statues, cause by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }
}
