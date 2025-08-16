package property.controller.Implementation;

import common.controller.base.BaseController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
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
import property.controller.Internal.InternalPropertyStatusController;
import property.domain.dto.propertyStatus.ReqCreatePropertyStatusDto;
import property.domain.dto.propertyStatus.ReqUpdatePropertyStatusDto;
import property.domain.dto.propertyStatus.ResEntryPropertyStatusDto;
import property.domain.mapper.PropertyStatusMapper;
import property.service.declare.DeclarePropertyStatusService;
import property.service.internal.InternalPropertyStatusService;

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
@Path("/property-status")
@ApplicationScoped
public class PropertyStatusController extends BaseController implements InternalPropertyStatusController {

    private InternalPropertyStatusService propertyStatusService;
    private DeclarePropertyStatusService declarePropertyStatusService;
    private PropertyStatusMapper propertyStatusMapper;

    @Inject
    public PropertyStatusController(InternalPropertyStatusService propertyStatusService, DeclarePropertyStatusService declarePropertyStatusService, PropertyStatusMapper propertyStatusMapper) {
        this.propertyStatusService = propertyStatusService;
        this.declarePropertyStatusService = declarePropertyStatusService;
        this.propertyStatusMapper = propertyStatusMapper;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new property status", description = "Create new property status")
    @Transactional
    @Override
    public Response createPropertyStatus(@Valid ReqCreatePropertyStatusDto reqCreatePropertyStatusDto) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyStatusService.createNewPropertyStatus(reqCreatePropertyStatusDto, userId)
                .fold(
                        error -> {
                            ErrorResponse theError = new ErrorResponse(
                                    "Failed to create new property status",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theError.getStatusCode())
                                    .entity(theError)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyStatusDto> payload = new SuccessResponse<>(
                                    "property status create successfully",
                                    propertyStatusMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(payload)
                                    .build();
                        }
                );
    }

    @PUT
    @Path("/{propertyStatusId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update property status", description = "Update property status")
    @Transactional
    @Override
    public Response updatePropertyStatus(@Valid ReqUpdatePropertyStatusDto reqUpdatePropertyStatusDto, @PathParam("propertyStatusId") UUID propertyStatusId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyStatusService.updatePropertyStatus(reqUpdatePropertyStatusDto, propertyStatusId, userId)
                .fold(
                        error -> {
                            ErrorResponse theError = new ErrorResponse(
                                    "Failed to update new property status",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theError.getStatusCode())
                                    .entity(theError)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyStatusDto> payload = new SuccessResponse<>(
                                    "property status update Successfully" ,
                                    propertyStatusMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(payload)
                                    .build();
                        }
                );
    }


    @DELETE
    @Path("/{propertyStatusId}")
    @Transactional
    @Operation(summary = "Delete property status", description = "Delete property status")
    @Override
    public Response deletePropertyStatus(@PathParam("propertyStatusId") UUID propertyStatusId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyStatusService.deletePropertyStatus(propertyStatusId, userId)
                .fold(
                        error -> {
                            ErrorResponse theError = new ErrorResponse(
                                    "Failed to delete property status",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theError.getStatusCode())
                                    .entity(theError)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "delete property status operation:",
                                    success
                            );
                            return Response
                                    .status(success ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @GET
    @Path("/{propertyStatusId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find property status by id", description = "Find property status by id")
    @Override
    public Response findPropertyStatusById(@RequestBody(required = false) @PathParam("propertyStatusId") UUID propertyStatusId) {
        UUID userId = getCurrentUserIdOrThrow();
        return declarePropertyStatusService.findPropertyStatusByIdAndUserId(propertyStatusId, userId)
                .fold(
                        error -> {
                            ErrorResponse theError = new ErrorResponse(
                                    "Failed to find property status",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theError.getStatusCode())
                                    .entity(theError)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyStatusDto> successResponse = new SuccessResponse<>(
                              "property status fetch successfully",
                              propertyStatusMapper.toDto(success)
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
    @Operation(summary = "Find all property statuses", description = "Find all property statuses")
    @Override
    public Response findAllPropertyStatuses(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ) {
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder()
                .page(page).size(size)
                .sortBy(sortBy).sortDirection(sortDirection)
                .build();
        return propertyStatusService.findAllPropertyStatues(userId, query)
                .fold(
                        error -> {
                            ErrorResponse theError = new ErrorResponse(
                                    "Failed to fetch property status",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theError.getStatusCode())
                                    .entity(theError)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryPropertyStatusDto>> payload = new SuccessResponse<>(
                                    "property status fetch successfully",
                                    propertyStatusMapper.toResListBaseDto("fetch successfully", success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(payload)
                                    .build();
                        }
                );
    }
}
