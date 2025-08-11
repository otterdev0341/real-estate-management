package contact.controller.implementation;

import common.controller.base.BaseController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.implementation.antonation.validator.ValidUUID;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import contact.controller.internal.InternalContactController;
import contact.domain.dto.contact.ReqCreateContactDto;
import contact.domain.dto.contact.ReqUpdateContactDto;
import contact.domain.dto.contact.ResEntryContactDto;
import contact.domain.dto.query.ContactQuery;
import contact.domain.mapper.ContactMapper;
import contact.service.internal.InternalContactService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;

import java.util.UUID;


@ApplicationScoped
public class ContactController extends BaseController implements InternalContactController {

    private final InternalContactService contactService;
    private final ContactMapper contactMapper;


    @Inject
    public ContactController(InternalContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Override
    public Response createContact(@Valid ReqCreateContactDto contactDto) {

        UUID userId = getCurrentUserIdOrThrow();

        return contactService.createNewContact(userId, contactDto)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to create new contact",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(Response.Status.BAD_REQUEST)
                                    .entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntryContactDto> successResponse = new SuccessResponse<>(
                                    "create new success",
                                    success
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(successResponse).build();
                        }
                );
    }


    @PUT
    @Path("/{contactId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Override
    public Response updateContact(
            @Valid ReqUpdateContactDto contactDto,
            @PathParam("contactId")  @ValidUUID UUID contactId
    ) {

        UUID userID = getCurrentUserIdOrThrow();

        return contactService.updateContact(userID, contactId, contactDto)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to update contact",
                                    error.message(),
                                    Response.Status.BAD_REQUEST

                            );
                            return Response
                                    .status(Response.Status.BAD_REQUEST)
                                    .entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntryContactDto> successResponse = new SuccessResponse<>(
                                    "update contact success",
                                    success
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse).build();
                        }
                );
    }


    @DELETE
    @Path("/{contactId}")
    @Transactional
    @Override
    public Response deleteContact(@PathParam("contactId") @ValidUUID UUID contactId) {
        UUID userId = getCurrentUserIdOrThrow();

        return contactService.deleteContact(userId, contactId)
                .fold(
                  error -> {
                      ErrorResponse errorResponse = new ErrorResponse(
                              "fail to delete contact",
                              error.message(),
                              Response.Status.BAD_REQUEST
                      );
                      return Response
                              .status(Response.Status.BAD_REQUEST)
                              .entity(errorResponse).build();
                  },
                  success -> {
                      SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                              "Contact Type Delete Operation: " + success,
                              success
                      );
                      return Response
                              .status(success ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                              .entity(successResponse)
                              .build();
                  }
                );
    }


    @GET
    @Path("/{contactId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findContactById(@PathParam("contactId") @ValidUUID UUID contactId) {
        UUID userId = getCurrentUserIdOrThrow();

        return contactService.findTheContactByIdAndUserId(contactId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Contact Type Fetch Failed reason by ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(Response.Status.BAD_REQUEST)
                                    .entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntryContactDto> successResponse = new SuccessResponse<>(
                                    "Contact Type Fetch Successfully",
                                    success.orElse(null)
                            );
                            return Response
                                    .status(success.isPresent() ? Response.Status.OK : Response.Status.NOT_FOUND)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllContacts(
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, businessName, contactTypeDetail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ) {
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder().page(page).size(size).sortBy(sortBy).sortDirection(sortDirection).build();
        return contactService.findAllContactsByUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Contact Type Fetch Failed reason by ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(Response.Status.BAD_REQUEST)
                                    .entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryContactDto>> payload = new SuccessResponse<>(
                                    "Contact Type Fetch Successfully",
                                    success
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(payload).build();
                        }
                );
    }
}
