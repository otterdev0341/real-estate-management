package memo.controller.implementation;

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
import memo.controller.internal.InternalMemoTypeController;
import memo.domain.dto.memoType.ReqCreateMemoTypeDto;
import memo.domain.dto.memoType.ReqUpdateMemoTypeDto;
import memo.domain.dto.memoType.ResEntryMemoTypeDto;
import memo.domain.mapper.MemoTypeMapper;
import memo.service.declare.DeclareMemoTypeService;
import memo.service.internal.InternalMemoTypeService;
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
@Path("/memo-type")
@ApplicationScoped
public class MemoTypeController extends BaseController implements InternalMemoTypeController {

    private final InternalMemoTypeService memoTypeService;
    private final DeclareMemoTypeService declareMemoTypeService;
    private final MemoTypeMapper memoTypeMapper;

    @Inject
    public MemoTypeController(InternalMemoTypeService memoTypeService, DeclareMemoTypeService declareMemoTypeService, MemoTypeMapper memoTypeMapper) {
        this.memoTypeService = memoTypeService;
        this.declareMemoTypeService = declareMemoTypeService;
        this.memoTypeMapper = memoTypeMapper;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new memo type", description = "Create new memo type")
    @Override
    public Response createMemoType(@Valid ReqCreateMemoTypeDto memoTypeDto) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoTypeService.createNewMemoTypeType(memoTypeDto, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                "Failed to create new memo type",
                                error.message(),
                                Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            ResEntryMemoTypeDto resEntryMemoTypeDto = memoTypeMapper.toDto(success);
                            SuccessResponse<ResEntryMemoTypeDto> successResponse = new SuccessResponse<>(
                                    "Memo Type Create Successfully",
                                    resEntryMemoTypeDto
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @PUT
    @Path("/{memoTypeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Override
    public Response updateMemoType(@Valid ReqUpdateMemoTypeDto memoTypeDto, @PathParam("memoTypeId") UUID memoTypeId) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoTypeService.updateMemoTypeType(memoTypeDto, userId, memoTypeId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to update memo type",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            ResEntryMemoTypeDto payload = memoTypeMapper.toDto(success);
                            SuccessResponse<ResEntryMemoTypeDto> response = new SuccessResponse<>(
                                    "Memo Type Update Successfully",
                                    payload
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(response)
                                    .build();
                        }
                );
    }

    @DELETE
    @Path("/{memoTypeId}")
    @Transactional
    @Override
    public Response deleteMemoType(@RequestBody(required = false) @PathParam("memoTypeId") UUID memoTypeId) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoTypeService.deleteMemoTypeTypeByIdAndUserId(memoTypeId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete memo type",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<Boolean> response = new SuccessResponse<>(
                                    "Memo Type Delete Operation",
                                    success
                            );
                            return Response
                                    .status(success ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST)
                                    .entity(response)
                                    .build();
                        }
                );
    }

    @GET
    @Path("/{memoTypeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findMemoTypeById(@RequestBody(required = false) @PathParam("memoTypeId") UUID memoTypeId) {
        UUID userId = getCurrentUserIdOrThrow();
        return declareMemoTypeService.findMemoTypeByIdAndUserId(memoTypeId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to fetch memo type",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            ResEntryMemoTypeDto dto = memoTypeMapper.toDto(success);
                            SuccessResponse<ResEntryMemoTypeDto> response = new SuccessResponse<>(
                                "memo type fetch successfully",
                                dto
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(response)
                                    .build();
                        }
                );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response findAllMemoTypes(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ) {
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder().page(page).size(size).sortBy(sortBy).sortDirection(sortDirection).build();
        return memoTypeService.findAllMemoTypeTypeWithUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                              "Failed to fetch memo types",
                              error.message(),
                              Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            ResListBaseDto<ResEntryMemoTypeDto> dto = memoTypeMapper.toResListBaseDto("memo type list",success);
                            SuccessResponse<ResListBaseDto<ResEntryMemoTypeDto>> payload = new SuccessResponse<>(
                                    "memo type list fetch successfully",
                                    dto
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(payload)
                                    .build();
                        }
                );
    }
}
