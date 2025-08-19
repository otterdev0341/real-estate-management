package property.service.cross;

import com.spencerwi.either.Either;
import common.domain.entity.Memo;
import common.errorStructure.ServiceError;

import java.util.List;
import java.util.UUID;

public interface InternalMemoCrossPropertyService {
    Either<ServiceError, Boolean> assignMemoToProperty(UUID memoId, UUID propertyId, UUID userId);

    Either<ServiceError, Boolean> removeMemoFromProperty(UUID memoId, UUID propertyId, UUID userId);

    Either<ServiceError, List<Memo>> findAllMemosByPropertyId(UUID propertyId, UUID userId);
}
