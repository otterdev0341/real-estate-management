package property.controller.Implementation;

import com.spencerwi.either.Either;
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
import property.controller.Internal.InternalPropertyTypeController;
import property.domain.dto.propertyType.ReqCreatePropertyTypeDto;
import property.domain.dto.propertyType.ReqUpdatePropertyTypeDto;
import property.domain.dto.propertyType.ResEntryPropertyTypeDto;
import property.domain.mapper.PropertyTypeMapper;
import property.service.declare.DeclarePropertyTypeService;
import property.service.internal.InternalPropertyTypeService;

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
@Path("/property-type")
@ApplicationScoped
public class PropertyTypeController extends BaseController implements InternalPropertyTypeController {

    private final InternalPropertyTypeService propertyTypeService;
    private final DeclarePropertyTypeService declarePropertyTypeService;
    private final PropertyTypeMapper propertyTypeMapper;

    @Inject
    public PropertyTypeController(InternalPropertyTypeService propertyTypeService, PropertyTypeMapper propertyTypeMapper, DeclarePropertyTypeService declarePropertyTypeService) {
        this.propertyTypeService = propertyTypeService;
        this.propertyTypeMapper = propertyTypeMapper;
        this.declarePropertyTypeService = declarePropertyTypeService;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new property type", description = "Create new property type")
    @Transactional
    @Override
    public Response createPropertyType(@Valid ReqCreatePropertyTypeDto reqCreatePropertyTypeDto) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyTypeService.createNewPropertyType(reqCreatePropertyTypeDto, userId)
                .fold(error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to create new Property Type ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                },
                success -> {
                    SuccessResponse<ResEntryPropertyTypeDto> successResponse = new SuccessResponse<>(
                            "property type create successfully",
                            propertyTypeMapper.toDto(success)
                    );
                    return Response
                            .status(Response.Status.CREATED)
                            .entity(successResponse)
                            .build();

                }
        );
    }


    @PUT
    @Path("/{propertyTypeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update property type", description = "Update property type")
    @Transactional
    @Override
    public Response updatePropertyType(@Valid ReqUpdatePropertyTypeDto reqUpdatePropertyTypeDto, @PathParam("propertyTypeId")  UUID propertyTypeId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyTypeService.updatePropertyType(reqUpdatePropertyTypeDto, propertyTypeId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to update new Property Type ",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyTypeDto> successResponse = new SuccessResponse<>(
                                    "property type update successfully",
                                    propertyTypeMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @DELETE
    @Path("/{propertyTypeId}")
    @Transactional
    @Operation(summary = "Delete property type", description = "Delete property type")
    @Override
    public Response deletePropertyType(@PathParam("propertyTypeId") UUID propertyTypeId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyTypeService.deletePropertyType(propertyTypeId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete property type :",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<Boolean> payload = new SuccessResponse<>(
                                    "property type delete Operation:",
                                    success
                            );
                            return Response
                                    .status(success ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST)
                                    .entity(payload)
                                    .build();
                        }
                );
    }


    @GET
    @Path("/{propertyTypeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find property type by id", description = "Find property type by id")
    @Override
    public Response findPropertyTypeById(@PathParam("propertyTypeId") UUID propertyTypeId) {
        UUID userId = getCurrentUserIdOrThrow();
        return declarePropertyTypeService.findPropertyTypeByIdAndUserId(propertyTypeId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to find property type by id :",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();

                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyTypeDto> payload = new SuccessResponse<>(
                                    "property type fetch successfully",
                                    propertyTypeMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(payload)
                                    .build();
                        }
                );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find all property types", description = "Find all property types")
    @Override
    public Response findAllPropertyTypes(
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
        return propertyTypeService.findAllPropertyTypes(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to fetch property type :",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryPropertyTypeDto>> payload = new SuccessResponse<>(
                                    "property type fetch successfully",
                                    propertyTypeMapper.toResListBaseDto("property type fetch successfully",success)

                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(payload)
                                    .build();
                        }
                );
    }
}
