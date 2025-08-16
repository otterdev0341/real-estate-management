package property.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyType;
import common.errorStructure.RepositoryError;
import java.util.List;
import java.util.UUID;

public interface InternalPropertyTypeRepository {

    Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID propertyTypeId, UUID userId);

    Either<RepositoryError, PropertyType> createPropertyType(PropertyType propertyType);

    Either<RepositoryError, PropertyType> updatePropertyType(PropertyType propertyType);

    Either<RepositoryError, PropertyType> findPropertyTypeByIdAndUserId(UUID propertyTypeId, UUID userId);

    Either<RepositoryError, Boolean> deletePropertyTypeByIdAndUserId(UUID propertyTypeId, UUID userId);

    Either<RepositoryError, List<PropertyType>> findAllPropertyTypeWithUserId(UUID userId, BaseQuery query);

}
