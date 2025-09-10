package payment.controller.implementation;

import com.spencerwi.either.Either;
import common.controller.base.BaseController;
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
import payment.controller.internal.InternalPaymentController;
import payment.domain.dto.payment.ResEntryPaymentDto;
import payment.domain.dto.wrapper.ReqCreatePaymentWrapperForm;
import payment.domain.dto.wrapper.ReqUpdatePaymentWrapperForm;
import payment.domain.mapper.PaymentMapper;
import payment.service.internal.InternalPaymentTransactionService;

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
@Path("/payment")
@ApplicationScoped
public class PaymentTransactionController extends BaseController implements InternalPaymentController {

    private final InternalPaymentTransactionService paymentTransactionService;
    private final FileAssetManagementService fileAssetManagementService;
    private final PaymentMapper paymentMapper;
    private final FileDetailMapper fileDetailMapper;

    public PaymentTransactionController(
            @Named("paymentTransactionService") InternalPaymentTransactionService paymentTransactionService,
            @Named("paymentTransactionService") FileAssetManagementService fileAssetManagementService,
            PaymentMapper paymentMapper,
            FileDetailMapper fileDetailMapper
    ) {
        this.paymentTransactionService = paymentTransactionService;
        this.fileAssetManagementService = fileAssetManagementService;
        this.paymentMapper = paymentMapper;
        this.fileDetailMapper = fileDetailMapper;
    }



    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Create a new payment", description = "This endpoint allows the creation of a new payment with the provided details.")
    @Override
    public Response createNewPayment(@BeanParam @Valid ReqCreatePaymentWrapperForm reqCreatePaymentWrapperForm) {
        UUID userId = getCurrentUserIdOrThrow();
        return paymentTransactionService.createNewPaymentTransaction(reqCreatePaymentWrapperForm, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to create payment",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPaymentDto> successResponse = new SuccessResponse<>(
                                "payment record create successfully",
                                paymentMapper.toDto(success)
                            );
                            return Response.status(Response.Status.CREATED)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Path("/{paymentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find payment by ID", description = "This endpoint retrieves a payment by its unique identifier.")
    @Override
    public Response findPaymentById(@PathParam("paymentId") UUID paymentId) {
        UUID userId = getCurrentUserIdOrThrow();
        return paymentTransactionService.findPaymentTransactionByIdAndUserId(paymentId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Payment not found",
                                    error.message(),
                                    Response.Status.NOT_FOUND
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPaymentDto> successResponse = new SuccessResponse<>(
                                    "Payment retrieved successfully",
                                    paymentMapper.toDto(success)
                            );
                            return Response.ok(successResponse).build();
                        }
                );
    }





    @PUT
    @Path("/{paymentId}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Update payment", description = "This endpoint allows updating an existing payment with the provided details.")
    @Override
    public Response updatePayment(@BeanParam @Valid ReqUpdatePaymentWrapperForm reqUpdatePaymentWrapperForm, @PathParam("paymentId") UUID paymentId) {
        UUID userId = getCurrentUserIdOrThrow();
        return paymentTransactionService.updatePaymentTransaction(reqUpdatePaymentWrapperForm, paymentId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to update payment",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryPaymentDto> successResponse = new SuccessResponse<>(
                                    "Payment updated successfully",
                                    paymentMapper.toDto(success)
                            );
                            return Response.ok(successResponse).build();
                        }
                );
    }




    @DELETE
    @Path("/{paymentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete payment", description = "This endpoint allows deleting a payment by its unique identifier.")
    @Transactional
    @Override
    public Response deletePayment(@RequestBody(required = false) @PathParam("paymentId") UUID paymentId) {
        UUID userId = getCurrentUserIdOrThrow();
        return paymentTransactionService.deletePaymentTransactionById(paymentId, userId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete payment",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<Boolean> successResponse = new SuccessResponse<>(
                                    "Payment deleted Operation:",
                                    success
                            );
                            return Response.status(success ? Response.Status.NO_CONTENT : Response.Status.NOT_FOUND)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all payments", description = "This endpoint retrieves all payments associated with a specific property ID.")
    @Override
    public Response getAllPayment(
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
        return paymentTransactionService.findAllPaymentTransactionWithUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to retrieve payments",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response.status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "Payments retrieved successfully",
                                    paymentMapper.toDtoList(success)
                            );
                            return Response
                                    .status(Response.Status.OK)
                                    .entity(successResponse)
                                    .build();

                        }
                );
    }

    @GET
    @Path("/property/{propertyId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response getAllPaymentByPropertyId(@PathParam("propertyId") UUID propertyId) {
        UUID userId = getCurrentUserIdOrThrow();
        return paymentTransactionService.findAllPaymentByPropertyId(propertyId, userId)
                .fold(
                  error -> {
                      ErrorResponse errorResponse = new ErrorResponse(
                              "Failed to retrieve payments",
                              error.message(),
                              Response.Status.BAD_REQUEST
                      );
                      return Response.status(errorResponse.getStatusCode())
                              .entity(errorResponse)
                              .build();
                  },
                  success -> {
                      SuccessResponse<?> successResponse = new SuccessResponse<>(
                              "Payments retrieved successfully",
                              paymentMapper.toDtoList(success)
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
    @Path("/remove/{paymentId}/{fileId}")
    @Transactional
    @Override
    public Response deleteFileFromTarget(@PathParam("paymentId") UUID targetId, @PathParam("fileId") UUID targetFileId) {
        UUID userId = getCurrentUserIdOrThrow();
        return fileAssetManagementService.deleteFileByTargetAndFileId(targetId, userId, targetFileId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "Failed to delete file from payment transaction",
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
    @Path("{paymentId}/files/{criteria}")
    @Operation(summary = "Get all file by criteria", description = "Get all file by criteria")
    @Override
    public Response getAllFileByCriteria(@PathParam("paymentId") UUID targetId, @PathParam("criteria") String fileCase) {
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
