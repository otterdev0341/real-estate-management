package sale.controller.implementation;

import com.spencerwi.either.Either;
import common.controller.base.BaseController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.fileDetail.RequestAttachFile;
import common.domain.dto.fileDetail.ResEntryFileDetailDto;
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
import sale.controller.internal.InternalSaleTransactionController;
import sale.domain.dto.ReqCreateSaleDto;
import sale.domain.dto.ReqUpdateSaleDto;
import sale.domain.dto.ResEntrySaleDto;
import sale.domain.dto.form.ReqCreateSaleForm;
import sale.domain.dto.form.ReqUpdateSaleForm;
import sale.domain.mapper.SaleMapper;
import sale.service.internal.InternalSaleTransactionService;

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
@Path("/sale")
@ApplicationScoped
public class SaleTransactionController extends BaseController implements InternalSaleTransactionController {

    private final InternalSaleTransactionService saleTransactionService;
    private final FileAssetManagementService fileAssetManagementService;
    private final SaleMapper saleMapper;
    private final FileDetailMapper fileDetailMapper;

    @Inject
    public SaleTransactionController(
            @Named("saleTransactionService") InternalSaleTransactionService saleTransactionService,
            @Named("saleTransactionService") FileAssetManagementService fileAssetManagementService,
            SaleMapper saleMapper,
            FileDetailMapper fileDetailMapper
    ) {
        this.saleTransactionService = saleTransactionService;
        this.fileAssetManagementService = fileAssetManagementService;
        this.saleMapper = saleMapper;
        this.fileDetailMapper = fileDetailMapper;
    }


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new sale transaction", description = "Create new sale transaction")
    @Transactional
    @Override
    public Response createNewSaleTransaction(@Valid ReqCreateSaleForm reqCreateSaleForm) {
        UUID userId = getCurrentUserIdOrThrow();
        ReqCreateSaleDto reqCreateSaleDto = saleMapper.tryFormToDto(reqCreateSaleForm);
        return saleTransactionService.createNewSaleTransaction(reqCreateSaleDto, userId)
                .fold(error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Fail to create user",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntrySaleDto> successResponse = new SuccessResponse<>(
                                    "sale record create successfully",
                                        saleMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(successResponse)
                                    .build();
                        });
    }

    @PUT
    @Path("/{saleTransactionId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update sale transaction", description = "Update sale transaction")
    @Transactional
    @Override
    public Response updateNewSaleTransaction(@Valid ReqUpdateSaleForm reqUpdateSaleForm, @PathParam("saleTransactionId") UUID saleTransactionId) {
        UUID userId = getCurrentUserIdOrThrow();
        ReqUpdateSaleDto reqUpdateSaleDto = saleMapper.tryFormToDto(reqUpdateSaleForm);
        return saleTransactionService.updateSaleTransaction(reqUpdateSaleDto, saleTransactionId ,userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to update sale record",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntrySaleDto> successResponse = new SuccessResponse<>(
                                    "sale record update successfully",
                                    saleMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Path("/{saleTransactionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find sale transaction", description = "Find sale transaction")
    @Override
    public Response findSaleTransactionById( @RequestBody(required = false) @PathParam("saleTransactionId") UUID saleTransactionId) {
        UUID userId = getCurrentUserIdOrThrow();
        return saleTransactionService.findSaleTransactionByIdWithUserId(saleTransactionId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to find sale record",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResEntrySaleDto> successResponse = new SuccessResponse<>(
                                    "sale record find successfully",
                                    saleMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @DELETE
    @Path("/{saleTransactionId}")
    @Operation(summary = "Delete sale transaction", description = "Delete sale transaction")
    @Transactional
    @Override
    public Response deleteSaleTransaction(@RequestBody(required = false) @PathParam("saleTransactionId")  UUID saleTransactionId) {
        UUID userId = getCurrentUserIdOrThrow();
        return saleTransactionService.deleteSaleTransaction(saleTransactionId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete sale record",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "sale record delete operation:",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find all sale transaction", description = "Find all sale transaction")
    @Override
    public Response findAllSaleTransaction(
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
        return saleTransactionService.findAllSaleTransactionWithUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to retrieved sale transaction",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntrySaleDto>> successResponse = new SuccessResponse<>(
                                    "sale record retrieved successfully",
                                    saleMapper.toResListBaseDto("sale record retrieved successfully", success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @POST
    @Path("/attach/{targetId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Attach file to target", description = "Attach file to target")
    @Transactional
    @Override
    public Response attachFileToTarget(@PathParam("targetId") UUID targetId, @BeanParam @Valid RequestAttachFile targetFile) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.attachFileToTarget(targetId, userId, targetFile.getFile().getFirst())
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to attach file to target",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "file attach operation",
                                    success
                            );
                            return Response
                                    .status(success ? Response.Status.OK : Response.Status.BAD_REQUEST)
                                    .entity(successResponse)
                                    .build();

                        }
                );
    }

    @DELETE
    @Path("/remove/{saleId}/{fileId}")
    @Transactional
    @Override
    public Response deleteFileFromTarget(@RequestBody(required = false) @PathParam("saleId") UUID targetId, @PathParam("fileId") UUID targetFileId) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.deleteFileByTargetAndFileId(targetId, userId, targetFileId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete file from sale transaction",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "file delete operation:",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{saleId}/files/{criteria}")
    @Operation(summary = "Get all file by criteria", description = "Get all file by criteria")
    @Override
    public Response getAllFileByCriteria(@PathParam("saleId") UUID targetId, @PathParam("criteria") String fileCase) {
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
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to get all file by criteria",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "file retrieved successfully",
                                    fileDetailMapper.toDto(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }
}
