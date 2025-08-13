package property.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyStatus;
import common.domain.entity.PropertyType;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalPropertyStatusRepository {

    Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID propertyStatusId, UUID userId);

    Either<RepositoryError, PropertyStatus> createPropertyStatus(PropertyStatus propertyStatus);

    Either<RepositoryError, PropertyStatus> updatePropertyStatus(PropertyStatus propertyStatus);

    Either<RepositoryError, PropertyStatus> findPropertyStatusAndUserId(UUID propertyStatusId, UUID userId);

    Either<RepositoryError, List<PropertyStatus>> findAllPropertyStatusWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deletePropertyStatusByIdAndUserId(UUID propertyStatusId, UUID userId);



}
