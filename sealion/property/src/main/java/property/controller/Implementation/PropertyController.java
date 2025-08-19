package property.controller.Implementation;

import com.spencerwi.either.Either;
import common.controller.base.BaseController;
import common.controller.declare.FileAssetManagementController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.fileDetail.RequestAttachFile;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import memo.domain.dto.memo.ResEntryMemoDto;
import memo.domain.mapper.MemoMapper;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import property.controller.Internal.cross.InternalMemoCrossPropertyController;
import property.controller.Internal.property.InternalPropertyController;
import property.controller.Internal.cross.InternalPropertyCrossPropertyStatus;
import property.domain.dto.property.ReqCreatePropertyDto;
import property.domain.dto.property.ReqUpdatePropertyDto;
import property.domain.dto.property.ResEntryPropertyDto;
import property.domain.dto.property.form.ReqCreatePropertyForm;
import property.domain.dto.property.form.ReqUpdatePropertyForm;
import property.domain.dto.propertyType.ReqAssignPropertyType;
import property.domain.dto.propertyType.ResEntryPropertyTypeDto;
import property.domain.mapper.PropertyMapper;
import property.domain.mapper.PropertyTypeMapper;
import property.service.cross.InternalMemoCrossPropertyService;
import property.service.declare.DeclarePropertyService;
import property.service.internal.InternalPropertyService;

import java.util.List;
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
@Path("/property")
@ApplicationScoped
public class PropertyController extends BaseController implements InternalPropertyController, InternalPropertyCrossPropertyStatus, FileAssetManagementController, InternalMemoCrossPropertyController {

    private final InternalPropertyService propertyService;
    private final DeclarePropertyService declarePropertyService;
    private final FileAssetManagementService fileAssetManagementService;
    private final InternalMemoCrossPropertyService memoCrossPropertyService;
    private final PropertyMapper propertyMapper;
    private final PropertyTypeMapper propertyTypeMapper;
    private final MemoMapper memoMapper;



    @Inject
    public PropertyController(
            @Named("propertyService") InternalPropertyService propertyService,
            @Named("propertyService") DeclarePropertyService declarePropertyService,
            @Named("propertyService") FileAssetManagementService fileAssetManagementService,
            @Named("propertyService") InternalMemoCrossPropertyService memoCrossPropertyService,
            PropertyMapper propertyMapper,
            PropertyTypeMapper propertyTypeMapper,
            MemoMapper memoMapper
    ) {
        this.propertyService = propertyService;
        this.declarePropertyService = declarePropertyService;
        this.fileAssetManagementService = fileAssetManagementService;
        this.propertyMapper = propertyMapper;
        this.propertyTypeMapper = propertyTypeMapper;
        this.memoCrossPropertyService = memoCrossPropertyService;
        this.memoMapper = memoMapper;
    }

    @POST
    @Path("/attach/{propertyId}")
    @Operation(description = "attach file to target by giving propertyId", summary = "attach file to target both value is required!!")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Override
    public Response attachFileToTarget(@PathParam("propertyId") UUID targetId, @BeanParam @Valid RequestAttachFile targetFile) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.attachFileToTarget(targetId, userId, targetFile.getFile().getFirst())
                .fold(
                        error -> {
                            ErrorResponse theErrorResponse = new ErrorResponse(
                                    "Failed to attach file with property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theErrorResponse.getStatusCode())
                                    .entity(theErrorResponse)
                                    .build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> theSuccessRes = new SuccessResponse<>(
                                    "Attach file to property:",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.OK : Response.Status.BAD_REQUEST)
                                    .entity(theSuccessRes)
                                    .build();
                        }
                );
    }

    @DELETE
    @Path("/remove/{propertyId}/{fileId}")
    @Transactional
    @Operation(description = "remove file from property", summary = "remove file by id from property, both parameter is required!!")
    @Override
    public Response deleteFileFromTarget(@PathParam("propertyId") UUID targetId, @PathParam("fileId") UUID targetFileId) {
        UUID user = getCurrentUserIdOrThrow();
        return fileAssetManagementService.deleteFileByTargetAndFileId(targetId, user, targetFileId)
                .fold(
                        error -> {
                            ErrorResponse theErrorResponse = new ErrorResponse(
                                    "Failed to delete file from property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(theErrorResponse.getStatusCode())
                                    .entity(theErrorResponse)
                                    .build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> theSuccessRes = new SuccessResponse<>(
                                    "Remove file from property:",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.OK : Response.Status.BAD_REQUEST)
                                    .entity(theSuccessRes)
                                    .build();
                        }
                );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{propertyId}/files/{criteria}")
    @Operation(description = "find files related by criteria as image|pdf|other|all", summary = "find files related by criteria both value is required!!")
    @Override
    public Response getAllFileByCriteria(@PathParam("propertyId") UUID targetId, @PathParam("criteria")  String fileCase) {
        UUID userId = getCurrentUserIdOrThrow();
        Either<String, FileCaseSelect> fileCaseEth = FileCaseSelect.fromString(fileCase.trim());

        if (fileCaseEth.isLeft()) {
            ErrorResponse criteriaResponse = new ErrorResponse(
                    "criteria not correct allow only image|pdf|other|all",
                    fileCaseEth.getLeft(),
                    Response.Status.BAD_REQUEST
            );
            return Response
                    .status(criteriaResponse.getStatusCode())
                    .entity(criteriaResponse)
                    .build();
        }

        return fileAssetManagementService.getAllFileByCriteria(targetId, userId, fileCaseEth.getRight())
                .fold(
                        error -> {
                            ErrorResponse fetchFileErrorResponse = new ErrorResponse(
                                    "Fail to fetch file related to property cause by:",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response
                                    .status(fetchFileErrorResponse.getStatusCode())
                                    .entity(fetchFileErrorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<List<FileDetail>> fetchSuccessResponse = new SuccessResponse<>(
                                    "Fetch file related to property successfully",
                                    success
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(fetchSuccessResponse)
                                    .build();
                        }
                );

    }


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "create property by accept form data", summary = "create new property")
    @Override
    public Response createProperty(@Valid ReqCreatePropertyForm reqCreatePropertyForm) {
        UUID userId = getCurrentUserIdOrThrow();
        ReqCreatePropertyDto reqCreatePropertyDto = propertyMapper.tryFormToDto(reqCreatePropertyForm);

        return propertyService.createNewProperty(reqCreatePropertyDto, userId)
                .fold(error -> {
                    ErrorResponse theErrorRes = new ErrorResponse(
                            "Failed to create new property",
                            error.message(),
                            Response.Status.BAD_REQUEST
                    );
                    return Response
                            .status(theErrorRes.getStatusCode())
                            .entity(theErrorRes)
                            .build();

                },
            success -> {
                    SuccessResponse<ResEntryPropertyDto> successRes = new SuccessResponse<>(
                            "property create successfully",
                            propertyMapper.toDto(success)
                        );
                    return Response.status(Response.Status.CREATED)
                            .entity(successRes)
                            .build();
                        });
    }


    @PUT
    @Path("/{propertyId}")
    @Transactional
    @Operation(description = "update property detail with form", summary = "update property with field")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Override
    public Response updateProperty(@BeanParam @Valid ReqUpdatePropertyForm reqUpdatePropertyForm, @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        ReqUpdatePropertyDto reqUpdatePropertyDto = propertyMapper.tryFormToDto(reqUpdatePropertyForm);
        return propertyService.updateProperty(reqUpdatePropertyDto, propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse theError = new ErrorResponse(
                                    "Failed to update property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(theError.getStatusCode())
                                    .entity(theError)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyDto> successResponse = new SuccessResponse<>(
                                    "the property update successfully",
                                    propertyMapper.toDto(success)
                            );
                            return Response.status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @DELETE
    @Path("/{propertyId}")
    @Transactional
    @Operation(description = "delete property and all related file by giving propertyId", summary = "delete property by property id")
    @Override
    public Response deleteProperty(@RequestBody(required = false) @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyService.deleteProperty(propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete property by id",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "delete property operation",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.NO_CONTENT : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Path("/{propertyId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "find property by id", summary = "find property by id")
    @Override
    public Response findPropertyById(@RequestBody(required = false) @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return declarePropertyService.findPropertyByIdAndUserId(propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to find property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();

                        },
                        success -> {
                            SuccessResponse<ResEntryPropertyDto> successResponse = new SuccessResponse<>(
                                    "find property successfully",
                                    propertyMapper.toDto(success)
                            );
                            return Response.status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();

                        }
                );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "find all properties", summary = "find all properties")
    @Override
    public Response findAllProperties(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ) {
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder().page(page).size(size).sortBy(sortBy).sortDirection(sortDirection).build();
        return propertyService.findAllProperties(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to fetch property:",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryPropertyDto>> successResponse = new SuccessResponse<>(
                                    "fetch property successfully",
                                    propertyMapper.toResListBaseDto("property list", success)
                            );
                            return Response.status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }

                );
    }

    @PUT
    @Path("/{propertyId}/assign/propertyType")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(description = "assign property type to property", summary = "assign property type to property")
    @Override
    public Response assignPropertyTypeToProperty(@PathParam("propertyId") UUID propertyId, @Valid ReqAssignPropertyType reqAssignPropertyType) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyService.assignPropertyTypeToProperty(propertyId, reqAssignPropertyType.getPropertyTypes(), userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to assign property type to property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryPropertyTypeDto>> successResponse = new SuccessResponse<>(
                                    "assign property type to property successfully",
                                    propertyTypeMapper.toResListBaseDto("list of property that persist to property",success)
                            );
                            return Response.status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Path("/{propertyId}/propertyType")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "fetch all property type from property", summary = "get all property type relate by propertyId")
    @Override
    public Response findAllPropertyTypesByPropertyId(@RequestBody(required = false) @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyService.findAllPropertyTypesByPropertyId(propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to fetch property type by property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();

                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryPropertyTypeDto>> successResponse = new SuccessResponse<>(
                                    "property type fetch successfully",
                                    propertyTypeMapper.toResListBaseDto("property type list",success)
                            );
                            return Response.status(Response.Status.OK).entity(successResponse).build();
                        }
                );
    }

    @DELETE
    @Path("/{propertyTypeId}/{propertyId}")
    @Transactional
    @Operation(description = "remove property type from property", summary = "remove property type from property")
    @Override
    public Response removePropertyTypeFromProperty(@PathParam("propertyTypeId") UUID propertyTypeId, @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return propertyService.removePropertyTypeFromProperty(userId, propertyTypeId, propertyId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to remove property type from property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "remove property type from property operation:",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.NO_CONTENT : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse).build();
                        }
                );
    }


    @POST
    @Path("/{memoId}/{propertyId}")
    @Transactional
    @Operation(description = "assign memo to property", summary = "assign memo to property")
    @Override
    public Response assignMemoToProperty(@RequestBody(required = false) @PathParam("memoId") UUID memoId, @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoCrossPropertyService.assignMemoToProperty(memoId, propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to assign memo to property"
                                    , error.message()
                                    , Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "assign memo to property operation",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse).build();
                        }
                );
    }


    @DELETE
    @Path("/{memoId}/{propertyId}")
    @Transactional
    @Operation(description = "remove memo from property", summary = "remove memo from property")
    @Override
    public Response removeMemoFromProperty(@RequestBody(required = false) @PathParam("memoId") UUID memoId, @PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoCrossPropertyService.removeMemoFromProperty(memoId, propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to remove memo from property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "remove memo from property operation",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse).build();
                        }
                );
    }

    @GET
    @Path("/{propertyId}/memos")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "get all memos related by property", summary = "get all memos related by property")
    @Override
    public Response findAllMemosByPropertyId(@RequestBody(required = false) UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoCrossPropertyService.findAllMemosByPropertyId(propertyId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to fetch memos related by property",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryMemoDto>> successResponse = new SuccessResponse<>(
                                    "fetch memos related by property successfully",
                                    memoMapper.toResListBaseDto("memo list", success)
                            );
                            return Response.status(Response.Status.OK).entity(successResponse).build();
                        }
                );
    }
} // end property controller
