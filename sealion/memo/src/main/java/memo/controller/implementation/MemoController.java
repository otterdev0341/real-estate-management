package memo.controller.implementation;

import com.spencerwi.either.Either;
import common.controller.base.BaseController;
import common.controller.declare.FileAssetManagementController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.fileDetail.RequestAttachFile;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.mapper.FileDetailMapper;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import memo.controller.internal.InternalMemoController;
import memo.domain.dto.memo.ReqCreateMemoDto;
import memo.domain.dto.memo.ReqUpdateMemoDto;
import memo.domain.dto.memo.ResEntryMemoDto;
import memo.domain.dto.memo.form.ReqCreateMemoForm;
import memo.domain.dto.memo.form.ReqUpdateMemoForm;
import memo.domain.mapper.MemoMapper;
import memo.service.declare.DeclareMemoService;
import memo.service.internal.InternalMemoService;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.util.List;
import java.util.Optional;
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
@Path("/memo")
@ApplicationScoped
public class MemoController extends BaseController implements InternalMemoController, FileAssetManagementController {

    private final InternalMemoService memoService;
    private final DeclareMemoService declareMemoService;
    private final FileAssetManagementService fileAssetManagementService;
    private final MemoMapper memoMapper;
    private final FileDetailMapper fileDetailMapper;

    @Inject
    public MemoController(
            @Named("memoService") InternalMemoService memoService,
            @Named("memoService") DeclareMemoService declareMemoService,
            @Named("memoService") FileAssetManagementService fileAssetManagementService,
            MemoMapper memoMapper,
            FileDetailMapper fileDetailMapper
    ) {
        this.memoService = memoService;
        this.declareMemoService = declareMemoService;
        this.fileAssetManagementService = fileAssetManagementService;
        this.memoMapper = memoMapper;
        this.fileDetailMapper = fileDetailMapper;
    }


    @POST
    @Path("/attach/{memoId}")
    @Operation(description = "attach file to target by giving memoId", summary = "attach file to target both value is required!!")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Transactional
    @Override
    public Response attachFileToTarget(@PathParam("memoId") UUID targetId, @BeanParam @Valid RequestAttachFile targetFile) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.attachFileToTarget(targetId, userId, targetFile.getFile().getFirst())
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                              "Fail to upload file to memo cause by:",
                              error.message(),
                              Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "upload file to memo operation: ",
                                    success
                            );
                            return Response
                                    .status(success ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }



    @DELETE
    @Path("/remove/{memoId}/{fileId}")
    @Transactional
    @Operation(description = "remove file from memo", summary = "remove file from memo both value is required!!")
    @Override
    public Response deleteFileFromTarget(@PathParam("memoId") UUID targetId, @PathParam("fileId") UUID targetFileId) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.deleteFileByTargetAndFileId(targetId, userId, targetFileId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Fail to delete file from memo cause by:",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();

                        },
                        operationResult -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "delete file from memo operation: ",
                                    operationResult
                            );
                            return Response
                                    .status(operationResult ? Response.Status.OK : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{memoId}/files/{criteria}")
    @Operation(description = "find files related by criteria as image|pdf|other|all", summary = "find files related by criteria both value is required!!")
    @Override
    public Response getAllFileByCriteria(@PathParam("memoId") UUID targetId, @PathParam("criteria") String fileCase) {
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
                                "Fail to fetch file related to memo cause by:",
                                error.message(),
                                Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response
                                    .status(fetchFileErrorResponse.getStatusCode())
                                    .entity(fetchFileErrorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<?> fetchSuccessResponse = new SuccessResponse<>(
                                    "Fetch file related to memo successfully",
                                    fileDetailMapper.toDto(success)
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
    @Transactional
    @Operation(description = "accept form with file attached and create new Memo", summary = "create new Memo")
    @Override
    public Response createMemo(@Valid ReqCreateMemoForm reqCreateMemoForm) {
        UUID userId = getCurrentUserIdOrThrow();
        ReqCreateMemoDto reqCreateMemoDto = memoMapper.tryFormToDto(reqCreateMemoForm);
        return memoService.createNewMemo(reqCreateMemoDto, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Fail to create new memo cause by:",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();

                        },
                        data -> {
                            SuccessResponse<ResEntryMemoDto> successResponse = new SuccessResponse<>(
                                    "create new memo successfully",
                                    memoMapper.toDto(data)
                            );
                            return Response.status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @PUT
    @Path("/{memoId}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(description = "update memo with form data, this will not accept any upload file with form", summary = "update memo")
    @Override
    public Response updateMemo(@Valid ReqUpdateMemoForm reqUpdateMemoForm, @PathParam("memoId") UUID memoId) {
        UUID userId = getCurrentUserIdOrThrow();
        ReqUpdateMemoDto reqUpdateMemoDto = memoMapper.tryFormToDto(reqUpdateMemoForm);
        return memoService.updateMemo(reqUpdateMemoDto, userId, memoId)
                .fold(error -> {
                    ErrorResponse errorResponse = new ErrorResponse(
                            "Fail to update memo cause by:",
                            error.message(),
                            Response.Status.BAD_REQUEST
                    );
                    return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                },
                data -> {
                    SuccessResponse<ResEntryMemoDto> payload = new SuccessResponse<>(
                            "update memo successfully",
                            memoMapper.toDto(data)
                    );
                    return Response.status(Response.Status.OK).entity(payload).build();
                });
    }

    @DELETE
    @Path("/{memoId}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "delete memo by id", summary = "delete memo")
    @Override
    public Response deleteMemo(@RequestBody(required = false) @PathParam("memoId") UUID memoId) {
        UUID userId = getCurrentUserIdOrThrow();
        return memoService.deleteMemoByIdAndUserId(memoId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete memo",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operationResult -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "delete memo operation: ",
                                    operationResult
                            );
                            return Response
                                    .status(operationResult ? Response.Status.NO_CONTENT : Response.Status.INTERNAL_SERVER_ERROR)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @GET
    @Path("/{memoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "find memo by id", summary = "find memo")
    @Override
    public Response findMemoById(@RequestBody(required = false) @PathParam("memoId") UUID memoId) {
        UUID userId = getCurrentUserIdOrThrow();
        return declareMemoService.findMemoByIdAndUserId(memoId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to find memo",
                                    error.message(),
                                    Response.Status.NOT_FOUND
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        data -> {
                            SuccessResponse<ResEntryMemoDto> payload = new SuccessResponse<>(
                                    "find memo successfully",
                                    memoMapper.toDto(data)
                            );
                            return Response.status(Response.Status.OK).entity(payload).build();
                        }
                );
    }



    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "find all memos", summary = "find all memos")
    @Override
    public Response findAllMemos(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ) {
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder().page(page).size(size).sortBy(sortBy).sortDirection(sortDirection).build();

        return memoService.findAllMemoWithUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to fetch memos",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();

                        },
                        listData -> {
                            SuccessResponse<ResListBaseDto<ResEntryMemoDto>> successResponse = new SuccessResponse<>(
                                    "fetch memo successfully",
                                    memoMapper.toResListBaseDto("memo list", listData)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }
}
