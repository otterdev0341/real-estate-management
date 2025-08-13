package contact.service.internal;

import java.util.Optional;
import java.util.UUID;
import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.errorStructure.ServiceError;
import contact.domain.dto.contactType.ReqCreateContactTypeDto;
import contact.domain.dto.contactType.ReqUpdateContactTypeDto;
import contact.domain.dto.contactType.ResEntryContactTypeDto;

public interface InternalContactTypeService {
    

    Either<ServiceError, ResEntryContactTypeDto> createNewContactType(UUID userId, ReqCreateContactTypeDto reqCreateContactTypeDto);

    Either<ServiceError, ResEntryContactTypeDto> updateContactType(UUID userId, UUID contactTypeId, ReqUpdateContactTypeDto reqUpdateContactTypeDto);

    Either<ServiceError, Boolean> deleteContactType(UUID userId, UUID contactTypeId);

    Either<ServiceError, ResListBaseDto<ResEntryContactTypeDto>> findAllContactTypesByUserId(UUID userId, BaseQuery query);

    //
    Either<ServiceError, Optional<ResEntryContactTypeDto>> findTheContactTypeByIdAndUserId(UUID contactTypeId, UUID userId);
}
