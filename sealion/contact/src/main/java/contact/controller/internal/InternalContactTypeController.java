package contact.controller.internal;

import common.domain.dto.query.BaseQuery;
import common.implementation.antonation.validator.ValidUUID;
import contact.domain.dto.contactType.ReqCreateContactTypeDto;
import contact.domain.dto.contactType.ReqUpdateContactTypeDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface InternalContactTypeController {

    Response createContactType(@Valid ReqCreateContactTypeDto contactTypeDto);

    Response updateContactType(@Valid ReqUpdateContactTypeDto contactTypeDto,  UUID contactTypeId);

    Response deleteContactType(UUID contactTypeId);

    Response findContactTypeById(UUID contactTypeId);

    Response findAllContactTypes(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );

}
