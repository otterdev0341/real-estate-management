package contact.service.internal;

import java.util.Optional;
import java.util.UUID;
import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.errorStructure.ServiceError;
import contact.domain.dto.contact.ReqCreateContactDto;
import contact.domain.dto.contact.ReqUpdateContactDto;
import contact.domain.dto.contact.ResEntryContactDto;
import contact.domain.dto.query.ContactQuery;

public interface InternalContactService {

    Either<ServiceError, ResEntryContactDto> createNewContact(UUID userId, ReqCreateContactDto reqCreateContactDto);

    Either<ServiceError, ResEntryContactDto> updateContact(UUID userId, UUID contactId, ReqUpdateContactDto reqUpdateContactDto);

    Either<ServiceError, Boolean> deleteContact(UUID userId, UUID contactId);

    Either<ServiceError, ResListBaseDto<ResEntryContactDto>> findAllContactsByUserId(UUID userId, ContactQuery query);

    Either<ServiceError, Optional<ResEntryContactDto>> findTheContactByIdAndUserId(UUID contactId, UUID userId);

}
