package property.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Property;
import common.errorStructure.RepositoryError;
import java.util.List;
import java.util.UUID;


public interface InternalPropertyRepository {

    Either<RepositoryError, Boolean> isExistByNameAndUserId(String name, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID propertyId, UUID userId);

    Either<RepositoryError, Property> createProperty(Property property);

    Either<RepositoryError, Property> updateProperty(Property property);

    Either<RepositoryError, Property> findPropertyByIdAndUserId(UUID propertyId, UUID userId);

    Either<RepositoryError, List<Property>> findAllPropertyWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deletePropertyByIdAndUserId(UUID propertyId, UUID userId);
}
