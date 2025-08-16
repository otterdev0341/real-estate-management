package property.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyType;
import common.domain.entity.User;
import common.errorStructure.ServiceError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import property.domain.dto.propertyType.ReqCreatePropertyTypeDto;
import property.domain.dto.propertyType.ReqUpdatePropertyTypeDto;
import property.repository.internal.InternalPropertyTypeRepository;
import property.service.declare.DeclarePropertyTypeService;
import property.service.internal.InternalPropertyTypeService;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class PropertyTypeService implements DeclarePropertyTypeService, InternalPropertyTypeService {

    private final DeclareUserService userService;
    private final InternalPropertyTypeRepository propertyTypeRepository;

    @Inject
    public PropertyTypeService(DeclareUserService userService, InternalPropertyTypeRepository propertyTypeRepository) {
        this.userService = userService;
        this.propertyTypeRepository = propertyTypeRepository;
    }

    @Override
    public Either<ServiceError, Boolean> isPropertyTypeExistByIdAndUserId(UUID propertyTypeId, UUID userId) {
        return propertyTypeRepository.isExistByIdAndUserId(propertyTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to check is property type exist or not cause by:" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, PropertyType> findPropertyTypeByIdAndUserId(UUID propertyTypeId, UUID userId) {
        return propertyTypeRepository.findPropertyTypeByIdAndUserId(propertyTypeId, userId)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Failed to find property type by id cause by " + error.message()));
                        } ,
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, PropertyType> createNewPropertyType(ReqCreatePropertyTypeDto reqCreatePropertyTypeDto, UUID userId) {
        // check is user exist : get user object
        // check is new detail exist
        // prepare property type object
        // persist
        return userService.findUserById(userId)
                .flatMap(
                        Either::left,
                        user -> {
                            return propertyTypeRepository.isExistByDetailAndUserId(reqCreatePropertyTypeDto.getDetail().trim(), userId)
                                    .mapRight(exist -> Pair.of(user, exist))
                                    .mapLeft(error -> new ServiceError.OperationFailed("Failed to check is new property type detail is exist, cause by: " + error.message()));
                        }
                )
                .flatMapRight(pair -> {
                    User user = pair.getLeft();
                    Boolean isNewPropertyTypeDetailExist = pair.getRight();
                    if(isNewPropertyTypeDetailExist) {
                        ServiceError theError = new ServiceError.DuplicateEntry("the new property type detail already exist");
                        return Either.left(theError);
                    }
                    PropertyType propertyType = PropertyType.builder().detail(reqCreatePropertyTypeDto.getDetail().trim()).createdBy(user).build();
                    return propertyTypeRepository.createPropertyType(propertyType)
                            .mapRight(persisted -> persisted)
                            .mapLeft(error -> new ServiceError.PersistenceFailed("Failed to persist new property type cause by: " + error.message()));

                });
    }

    @Override
    public Either<ServiceError, PropertyType> updatePropertyType(ReqUpdatePropertyTypeDto reqUpdatePropertyTypeDto, UUID propertyTypeId, UUID userId) {
        // check is property type exist
        // check is new name to update exist
        // prepare payload
        // persist
        return propertyTypeRepository.findPropertyTypeByIdAndUserId(propertyTypeId, userId)
                .mapLeft(error -> (ServiceError) new ServiceError.OperationFailed("Failed to find property type by id, cause by: " + error.message()))
                .flatMapRight(founded -> {
                    return propertyTypeRepository.isExistByDetailAndUserId(reqUpdatePropertyTypeDto.getDetail().trim(), userId)
                            .mapRight(exist -> Pair.of(founded, exist))
                            .mapLeft(error -> new ServiceError.OperationFailed("Failed to check is updated date ail is exist" + error.message()));
                })
                .flatMapRight(pair -> {
                    PropertyType targetPropertyType = pair.getLeft();
                    boolean isUpdateDetailExist = pair.getRight();
                    if (isUpdateDetailExist) {
                        ServiceError duplicateError = new ServiceError.DuplicateEntry("Update detail already exist");
                        return Either.left(duplicateError);
                    }
                    if (targetPropertyType.getDetail().trim().equals(reqUpdatePropertyTypeDto.getDetail().trim())) {
                        ServiceError sameDataError = new ServiceError.DuplicateEntry("Update detail is the same as currently value");
                        return Either.left(sameDataError);
                    }
                    targetPropertyType.setDetail(reqUpdatePropertyTypeDto.getDetail().trim());
                    return propertyTypeRepository.updatePropertyType(targetPropertyType)
                            .mapRight(success -> success)
                            .mapLeft(thisError -> new ServiceError.OperationFailed("Failed to update property type cause by: " + thisError.message()));
                });
    }

    @Override
    public Either<ServiceError, Boolean> deletePropertyType(UUID propertyTypeId, UUID userId) {
        return propertyTypeRepository.deletePropertyTypeByIdAndUserId(propertyTypeId, userId)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Failed to delete property type cause by " + error.message()));
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<PropertyType>> findAllPropertyTypes(UUID userId, BaseQuery query) {
        return propertyTypeRepository.findAllPropertyTypeWithUserId(userId, query)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Failed to fetch property types cause by " + error.message()));
                        },
                        Either::right
                );
    }
}
