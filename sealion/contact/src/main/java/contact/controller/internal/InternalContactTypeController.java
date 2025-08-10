package contact.controller.internal;

import common.domain.dto.query.BaseQuery;
import common.implementation.antonation.validator.ValidUUID;
import contact.domain.dto.contactType.ReqCreateContactTypeDto;
import contact.domain.dto.contactType.ReqUpdateContactTypeDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface InternalContactTypeController {

    Response createContactType(@Valid ReqCreateContactTypeDto contactTypeDto);

    Response updateContactType(@Valid ReqUpdateContactTypeDto contactTypeDto, @ValidUUID UUID contactTypeId);

    Response deleteContactType(@ValidUUID UUID contactTypeId);

    Response findContactTypeById(@ValidUUID UUID contactTypeId);

    Response findAllContactTypes(BaseQuery query);

}
