package contact.service.declare;

import java.util.Optional;
import java.util.UUID;
import com.spencerwi.either.Either;
import common.errorStructure.ServiceError;
import contact.domain.dto.contact.ResEntryContactDto;

public interface DeclareContactService {

    Either<ServiceError, Boolean> isContactExistWithUserId(UUID contactId, UUID userId);

    Either<ServiceError, Optional<ResEntryContactDto>> findContactByIdAndUserId(UUID contactId, UUID userId);
}
