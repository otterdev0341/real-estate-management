package property.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.PropertyStatus;
import common.errorStructure.ServiceError;

import java.util.UUID;

public interface DeclarePropertyStatusService {

    Either<ServiceError, Boolean> isPropertyStatusExistByIdAndUserId(UUID propertyStatusId, UUID userId);

    Either<ServiceError, PropertyStatus> findPropertyStatusByIdAndUserId(UUID propertyStatusId, UUID userId);

}
