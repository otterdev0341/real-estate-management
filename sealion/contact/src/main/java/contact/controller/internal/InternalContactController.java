package contact.controller.internal;

import common.domain.dto.query.BaseQuery;
import common.implementation.antonation.validator.ValidUUID;
import contact.domain.dto.contact.ReqCreateContactDto;
import contact.domain.dto.contact.ReqUpdateContactDto;
import contact.domain.dto.query.ContactQuery;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;



public interface InternalContactController {

    Response createContact(@Valid ReqCreateContactDto contactDto);

    Response updateContact(@Valid ReqUpdateContactDto contactDto, @ValidUUID UUID contactId);

    Response deleteContact(@ValidUUID UUID contactId);

    Response findContactById(@ValidUUID UUID contactId);

    Response findAllContacts(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );


}
