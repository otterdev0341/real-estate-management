package contact.controller.internal;

import contact.domain.dto.contact.ReqCreateContactDto;
import contact.domain.dto.contact.ReqUpdateContactDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;



public interface InternalContactController {

    Response createContact(@Valid ReqCreateContactDto contactDto);

    Response updateContact(@Valid ReqUpdateContactDto contactDto, UUID contactId);

    Response deleteContact(UUID contactId);

    Response findContactById(UUID contactId);

    Response findAllContacts(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );


}
