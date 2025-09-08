package investment.controller.Implementation;

import com.spencerwi.either.Either;
import common.controller.base.BaseController;
import common.domain.dto.fileDetail.RequestAttachFile;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.investment.InvestmentTransaction;
import common.domain.mapper.FileDetailMapper;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import investment.controller.Internal.InternalInvestmentController;
import investment.domain.dto.wrapper.ReqCreateInvestmentWrapperForm;
import investment.domain.dto.wrapper.ReqUpdateInvestmentWrapper;
import investment.domain.mapper.InvestmentMapper;
import investment.service.internal.InternalInvestmentTransactionService;
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

import javax.print.attribute.standard.Media;
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
@Path("/investment")
@ApplicationScoped
public class InvestmentController extends BaseController implements InternalInvestmentController {

    private final InternalInvestmentTransactionService investmentTransactionService;
    private final FileAssetManagementService fileAssetManagementService;
    private final FileDetailMapper fileDetailMapper;
    private final InvestmentMapper investmentMapper;

    @Inject
    public InvestmentController(
            @Named("investmentService") InternalInvestmentTransactionService investmentTransactionService,
            @Named("investmentService") FileAssetManagementService fileAssetManagementService,
            FileDetailMapper fileDetailMapper,
            InvestmentMapper investmentMapper
    ) {
        this.investmentTransactionService = investmentTransactionService;
        this.fileAssetManagementService = fileAssetManagementService;
        this.fileDetailMapper = fileDetailMapper;
        this.investmentMapper = investmentMapper;
    }



    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Create a new investment", description = "This endpoint allows the creation of a new investment with the provided details.")
    @Override
    public Response createNewInvestment(@BeanParam @Valid ReqCreateInvestmentWrapperForm reqCreateInvestmentWrapperForm) {
        UUID userId = getCurrentUserIdOrThrow();
        return investmentTransactionService.createNewInvestmentTransaction(reqCreateInvestmentWrapperForm, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Error occurred while create new investment transaction",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "new investment transaction created successfully",
                                    investmentMapper.toDto(success)
                            );
                            return Response.status(Response.Status.CREATED).entity(successResponse).build();
                        }
                );
    }

    @GET
    @Path("/{investmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "find investment by ID", summary = "Find investment by ID")
    @Override
    public Response findInvestmentById(@RequestBody(required = false) @PathParam("investmentId") UUID investmentId) {
        UUID userId = getCurrentUserIdOrThrow();
        return investmentTransactionService.findInvestmentTransactionByIdAndUserId(investmentId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Error occurred while fetching data",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "Data fetched successfully",
                                    investmentMapper.toDto(success)
                            );
                            return Response.status(Response.Status.OK).entity(successResponse).build();
                        }
                );
    }


    @PUT
    @Path("{investmentId}")
    @Transactional
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "update investment transaction", summary = "update investment transaction")
    @Override
    public Response updateInvestment(@BeanParam @Valid ReqUpdateInvestmentWrapper reqUpdateInvestmentWrapper, @PathParam("investmentId") UUID investmentId) {
        UUID userId = getCurrentUserIdOrThrow();
        return investmentTransactionService.updateInvestmentTransaction(reqUpdateInvestmentWrapper, investmentId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Error occurred while updating investment transaction",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "Investment transaction updated successfully",
                                    investmentMapper.toDto(success)
                            );
                            return Response.status(Response.Status.OK).entity(successResponse).build();
                        }
                );
    }

    @DELETE
    @Path("{investmentId}")
    @Transactional
    @Operation(description = "delete investment transaction", summary = "delete investment transaction")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response deleteInvestment(@RequestBody(required = false) @PathParam("investmentId") UUID investmentId) {
        UUID userId = getCurrentUserIdOrThrow();
        return investmentTransactionService.deleteInvestmentTransactionById(investmentId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Error occurred while deleting investment transaction",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        operation -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "Investment transaction deleted Operation:",
                                    operation
                            );
                            return Response
                                    .status(operation ? Response.Status.NO_CONTENT : Response.Status.BAD_REQUEST)
                                    .entity(successResponse).build();
                        }
                );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "get all investment", summary = "get all investment")
    @Override
    public Response getAllInvestment(
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
        return investmentTransactionService.findAllInvestmentTransactionWithUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Error occurred while fetching investment transactions",
                                    error.message(),
                                    Response.Status.INTERNAL_SERVER_ERROR
                            );
                            return Response.status(errorResponse.getStatusCode()).entity(errorResponse).build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "Investment transactions retrieved successfully",
                                    investmentMapper.toDtoList(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @POST
    @Path("/attach/{investmentTransactionId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "Attach file to target", description = "Attach file to target")
    @Transactional
    @Override
    public Response attachFileToTarget(@PathParam("investmentTransactionId") UUID targetId, @BeanParam @Valid RequestAttachFile targetFile) {
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
    @Path("/remove/{investmentTransactionId}/{fileId}")
    @Transactional
    @Operation(summary = "Delete file from target", description = "Delete file from target")
    @Override
    public Response deleteFileFromTarget(@PathParam("investmentTransactionId") UUID targetId, @PathParam("fileId") UUID targetFileId) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.deleteFileByTargetAndFileId(targetId, userId, targetFileId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete file from investment transaction",
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
    @Path("{investmentTransactionId}/files/{criteria}")
    @Operation(summary = "Get all file by criteria", description = "Get all file by criteria")
    @Override
    public Response getAllFileByCriteria(@PathParam("investmentTransactionId") UUID targetId, @PathParam("criteria") String fileCase) {
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
