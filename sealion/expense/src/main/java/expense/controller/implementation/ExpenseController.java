package expense.controller.implementation;

import common.controller.base.BaseController;
import common.domain.dto.query.BaseQuery;
import common.response.ErrorResponse;
import common.response.SuccessResponse;
import expense.controller.internal.InternalExpenseController;
import expense.domain.dto.expense.ReqCreateExpenseDto;
import expense.domain.dto.expense.ReqUpdateExpenseDto;
import expense.domain.dto.expense.ResEntryExpenseDto;
import expense.service.internal.InternalExpenseService;
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
@Path("/expense")
@ApplicationScoped
public class ExpenseController extends BaseController implements InternalExpenseController {

    private final InternalExpenseService expenseService;

    @Inject
    public ExpenseController(InternalExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Create Expense", description = "Create a new expense record.")
    @Override
    public Response createExpense(@Valid ReqCreateExpenseDto reqCreateExpenseDto){
        UUID userId = getCurrentUserIdOrThrow();
        return  expenseService.createNewExpense(userId, reqCreateExpenseDto)
                .fold(
                   error -> {
                       ErrorResponse errorResponse = new ErrorResponse(
                               "fail to create new expense",
                               error.message(),
                               Response.Status.BAD_REQUEST
                       );
                       return Response
                               .status(errorResponse.getStatusCode())
                               .entity(errorResponse)
                               .build();
                   },
                        success -> {
                            SuccessResponse<ResEntryExpenseDto> successResponse = new SuccessResponse<>(
                                    "success to create new expense",
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
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/{expenseId}")
    @Operation(summary = "Update Expense", description = "Update an existing expense record by ID.")
    @Override
    public Response updateExpense(@Valid ReqUpdateExpenseDto reqUpdateExpenseDto, @PathParam("expenseId") UUID expenseId){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseService.updateExpense(userId, expenseId, reqUpdateExpenseDto)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to update expense",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryExpenseDto> successResponse = new SuccessResponse<>(
                                    "success to update expense",
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
    @Path("/{expenseId}")
    @Transactional
    @Operation(summary = "Delete Expense", description = "Delete an expense record by ID.")
    @Override
    public Response deleteExpense(@PathParam("expenseId") UUID expenseId){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseService.deleteExpense(userId, expenseId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to delete expense",
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
                                    "Expense Delete Operation: " + success,
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
    @Path("/{expenseId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Find Expense by ID", description = "Retrieve an expense record by its ID.")
    @Override
    public Response findExpenseById(@RequestBody(required = false) @PathParam("expenseId") UUID expenseId){
        UUID userId = getCurrentUserIdOrThrow();
        return expenseService.findTheExpenseByIdAndUserId(userId, expenseId)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to find expense by id",
                                    error.message(),
                                    Response.Status.NOT_FOUND
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<ResEntryExpenseDto> successResponse = new SuccessResponse<>(
                                    "success to find expense by id",
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
    @Operation(summary = "Find All Expenses", description = "Retrieve all expense records with optional pagination and sorting.")
    @Override
    public Response findAllExpenses(
            @RequestBody(required = false)
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, businessName, contactTypeDetail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    ){
        UUID userId = getCurrentUserIdOrThrow();
        BaseQuery query = BaseQuery.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();
        return expenseService.findAllExpensesByUserId(userId, query)
                .fold(
                        error -> {
                            ErrorResponse errorResponse = new ErrorResponse(
                                    "fail to find all expenses",
                                    error.message(),
                                    Response.Status.BAD_REQUEST
                            );
                            return Response
                                    .status(errorResponse.getStatusCode())
                                    .entity(errorResponse)
                                    .build();
                        },
                        success -> {
                            SuccessResponse<?> successResponse = new SuccessResponse<>(
                                    "success to find all expenses",
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
