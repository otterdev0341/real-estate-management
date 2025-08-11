package contact.service.declare;

import java.util.Optional;
import java.util.UUID;
import com.spencerwi.either.Either;
import common.domain.entity.ContactType;
import common.errorStructure.ServiceError;

public interface DeclareContactTypeService {

    
    Either<ServiceError, Boolean> isContactTypeExistWithUserId(UUID contactTypeId, UUID userId);

    Either<ServiceError, ContactType> findContactTypeByIdAndUserId(UUID contactTypeId, UUID userId);
}
