package property.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Property;
import common.domain.entity.PropertyType;
import common.errorStructure.ServiceError;
import property.domain.dto.property.ReqCreatePropertyDto;
import property.domain.dto.property.ReqUpdatePropertyDto;

import java.util.List;
import java.util.UUID;

public interface InternalPropertyService {

    Either<ServiceError, Property> createNewProperty(ReqCreatePropertyDto reqCreatePropertyDto, UUID userId);

    Either<ServiceError, Property> updateProperty(ReqUpdatePropertyDto reqUpdatePropertyDto, UUID propertyId, UUID userId);

    Either<ServiceError, Boolean> deleteProperty(UUID propertyId, UUID userId);

    Either<ServiceError, List<Property>> findAllProperties(UUID userId, BaseQuery query);

    Either<ServiceError, List<PropertyType>> assignPropertyTypeToProperty(UUID propertyId, List<UUID> propertyTypeIds, UUID userId);

    Either<ServiceError, Boolean> removePropertyTypeFromProperty(UUID userId, UUID propertyTypeId, UUID propertyId);

    Either<ServiceError, List<PropertyType>> findAllPropertyTypesByPropertyId(UUID propertyId, UUID userId);
}
