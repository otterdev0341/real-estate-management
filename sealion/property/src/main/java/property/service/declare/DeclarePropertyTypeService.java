package property.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.PropertyType;
import common.errorStructure.ServiceError;
import java.util.UUID;

public interface DeclarePropertyTypeService {

    Either<ServiceError, Boolean> isPropertyTypeExistByIdAndUserId(UUID propertyTypeId, UUID userId);

    Either<ServiceError, PropertyType> findPropertyTypeByIdAndUserId(UUID propertyTypeId, UUID userId);

}
