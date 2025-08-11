package contact.controller.implementation;

import common.controller.base.BaseController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import contact.controller.internal.InternalContactTypeController;
import contact.domain.dto.contactType.ReqCreateContactTypeDto;
import contact.domain.dto.contactType.ReqUpdateContactTypeDto;
import contact.domain.dto.contactType.ResEntryContactTypeDto;
import contact.service.internal.InternalContactTypeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import java.util.UUID;



@SecuritySchemes(value = {
        @SecurityScheme(
                securitySchemeName = "jwt",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT"
        )
})
@SecurityRequirement(name = "jwt")
@Path("/contact-type")
@ApplicationScoped
public class ContactTypeController extends BaseController implements InternalContactTypeController {

    private final InternalContactTypeService contactTypeService;

    @Inject
    public ContactTypeController(InternalContactTypeService contactTypeService) {
        this.contactTypeService = contactTypeService;
    }


    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new contact type", description = "Create new contact type")
    @Override
    public Response createContactType(@Valid ReqCreateContactTypeDto contactTypeDto){
        UUID userId = getCurrentUserIdOrThrow();
        return contactTypeService.createNewContactType(userId, contactTypeDto)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Contact Type Create Failed reason by ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntryContactTypeDto> successResponse = new SuccessResponse<>(
                                    "Contact Type Create Successfully",
                                    success
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @PUT
    @Path("/{contactTypeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response updateContactType(@Valid ReqUpdateContactTypeDto contactTypeDto, @PathParam("contactTypeId") UUID contactTypeId)
    {
        UUID userId = getCurrentUserIdOrThrow();

        return contactTypeService.updateContactType(userId, contactTypeId, contactTypeDto)
                .fold(
                  error -> {
                      ErrorResponse errorResponse = new ErrorResponse(
                              "Contact Type Update Failed reason by ",
                              error.message(),
                              Response.Status.BAD_REQUEST
                      );
                      return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
                  },
                  success -> {
                      SuccessResponse<ResEntryContactTypeDto> successResponse = new SuccessResponse<>(
                              "Contact Type Update Successfully",
                              success
                      );
                      return Response
                              .status(Response.Status.OK)
                              .entity(successResponse)
                              .build();
                  }
                );
    }


    @DELETE
    @Path("/{contactTypeId}")
    @Override
    public Response deleteContactType(
            @RequestBody(required = false)
            @PathParam("contactTypeId") UUID contactTypeId
    )
    {
        UUID userId = getCurrentUserIdOrThrow();
        return contactTypeService.deleteContactType(userId, contactTypeId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Contact Type Delete Failed reason by ",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(errorResponse).build();
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
    @Path("/{contactTypeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findContactTypeById(@RequestBody(required = false) @PathParam("contactTypeId") UUID contactTypeId){
        UUID userId = getCurrentUserIdOrThrow();
        return contactTypeService.findTheContactTypeByIdAndUserId(contactTypeId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Contact Type Fetch Failed reason by ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntryContactTypeDto> successResponse;
                            if (success.isEmpty()) {
                                 successResponse = new SuccessResponse<>(
                                        "Contact Type Not Found",
                                        null
                                );
                                return Response
                                        .status(Response.Status.NOT_FOUND)
                                        .entity(successResponse)
                                        .build();
                            }
                            successResponse = new SuccessResponse<>(
                                    "Contact Type Fetch Successfully",
                                    success.get()
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllContactTypes(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    )
    {
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder()
                .page(page).size(size)
                .sortBy(sortBy).sortDirection(sortDirection)
                .build();
        return contactTypeService.findAllContactTypesByUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Contact Type Fetch Failed reason by ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST

                            );
                            return Response.status(Response.Status.BAD_REQUEST).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryContactTypeDto>> successResponse = new SuccessResponse<>(
                                    "Contact Type Fetch Successfully",
                                    success
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }
}
