package property.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Property;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalPropertyRepository {

    Either<RepositoryError, Boolean> isExistByNameAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID PropertyId, UUID userId);

    Either<RepositoryError, Property> createProperty(Property Property);

    Either<RepositoryError, Property> updateProperty(Property Property, UUID userId);

    Either<RepositoryError, Property> findPropertyAndUserId(UUID PropertyId, UUID userId);

    Either<RepositoryError, List<Property>> findAllPropertyWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deletePropertyByIdAndUserId(UUID PropertyId, UUID userId);
}
