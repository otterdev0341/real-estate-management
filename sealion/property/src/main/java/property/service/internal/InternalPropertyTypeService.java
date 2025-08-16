package property.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyType;
import common.errorStructure.ServiceError;
import property.domain.dto.propertyType.ReqCreatePropertyTypeDto;
import property.domain.dto.propertyType.ReqUpdatePropertyTypeDto;

import java.util.List;
import java.util.UUID;

public interface InternalPropertyTypeService {
    Either<ServiceError, PropertyType> createNewPropertyType(ReqCreatePropertyTypeDto reqCreatePropertyTypeDto, UUID userId);

    Either<ServiceError, PropertyType> updatePropertyType(ReqUpdatePropertyTypeDto reqUpdatePropertyTypeDto, UUID propertyTypeId, UUID userId);

    Either<ServiceError, Boolean> deletePropertyType(UUID propertyTypeId, UUID userId);

    Either<ServiceError, List<PropertyType>> findAllPropertyTypes(UUID userId, BaseQuery query);
}
