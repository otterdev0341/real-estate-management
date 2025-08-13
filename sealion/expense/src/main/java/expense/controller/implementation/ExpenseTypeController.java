package expense.controller.implementation;

import auth.service.declare.DeclareUserService;
import common.controller.base.BaseController;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import expense.controller.internal.InternalExpenseTypeController;
import expense.domain.dto.expenseType.ReqCreateExpenseTypeDto;
import expense.domain.dto.expenseType.ReqUpdateExpenseTypeDto;
import expense.domain.dto.expenseType.ResEntryExpenseTypeDto;
import expense.service.internal.InternalExpenseTypeService;
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
@Path("/expense-type")
@ApplicationScoped
public class ExpenseTypeController extends BaseController implements InternalExpenseTypeController {

    private final InternalExpenseTypeService expenseTypeService;

    @Inject
    public ExpenseTypeController(InternalExpenseTypeService expenseTypeService) {
        this.expenseTypeService = expenseTypeService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Create a new expense type", description = "Create a new expense type for the current user.")
    @Override
    public Response createExpenseType(@Valid ReqCreateExpenseTypeDto reqCreateExpenseTypeDto){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseTypeService.createNewExpenseType(userId, reqCreateExpenseTypeDto)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to create new expense type",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryExpenseTypeDto> successResponse = new SuccessResponse<>(
                                    "success to create new expense type",
                                    success
                            );
                            return Response
                                    .status(Response.Status.CREATED)
                                    .entity(successResponse)
                                    .build();
                        }
                );
    } // end class


    @PUT
    @Path("/{expenseTypeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Update an existing expense type", description = "Update an existing expense type for the current user.")
    @Override
    public Response updateExpenseType(@Valid ReqUpdateExpenseTypeDto reqUpdateExpenseTypeDto, @PathParam("expenseTypeId") UUID expenseTypeId){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseTypeService.updateExpenseType(userId, expenseTypeId, reqUpdateExpenseTypeDto)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to update expense type",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryExpenseTypeDto> successResponse = new SuccessResponse<>(
                                    "success to update expense type",
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
    @Path("/{expenseTypeId}")
    @Transactional
    @Operation(summary = "Delete an expense type", description = "Delete an expense type by its ID.")
    @Override
    public Response deleteExpenseType(@PathParam("expenseTypeId") UUID expenseTypeId){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseTypeService.deleteExpenseType(userId, expenseTypeId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to delete expense type",
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
                                    "Expense Type Delete Operation: " + success,
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
    @Path("/{expenseTypeId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find an expense type by ID", description = "Retrieve an expense type by its ID.")
    @Override
    public Response findExpenseTypeById(@RequestBody(required = false) @PathParam("expenseTypeId") UUID expenseTypeId){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseTypeService.findTheExpenseTypeWithUserId(userId, expenseTypeId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to find expense type by id",
                                    error.message(),
                                    Response.Status.NOT_FOUND
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryExpenseTypeDto> successResponse = new SuccessResponse<>(
                                    "success to find expense type by id",
                                    success
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
    @Operation(summary = "Find all expense types", description = "Retrieve all expense types with pagination and sorting options.")
    @Override
    public Response findAllExpenseTypes(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, businessName, contactTypeDetail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ){
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder().page(page).size(size).sortBy(sortBy).sortDirection(sortDirection).build();
        return expenseTypeService.findAllExpenseTypesByUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to find all expense types",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResListBaseDto<ResEntryExpenseTypeDto>> successResponse = new SuccessResponse<>(
                                    "success to find all expense types",
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
