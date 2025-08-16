package property.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.Property;
import common.errorStructure.ServiceError;

import java.util.UUID;

public interface DeclarePropertyService {

    Either<ServiceError, Boolean> isPropertyExistWithUserId(UUID propertyId, UUID userId);

    Either<ServiceError, Property> findPropertyByIdAndUserId(UUID propertyId, UUID userId);

}
