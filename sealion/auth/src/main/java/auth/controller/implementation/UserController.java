package auth.controller.implementation;


import auth.controller.internal.InternalUserController;
import auth.service.internal.InternalUserService;
import common.controller.base.BaseController;
import common.domain.dto.user.*;
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
import org.eclipse.microprofile.openapi.annotations.security.*;
import common.domain.mapper.UserMapper;
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
@ApplicationScoped
@Path("/users")
public class UserController extends BaseController implements InternalUserController {


    private final InternalUserService userService;
    private final UserMapper userMapper;
    

    @Inject
    public UserController(InternalUserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    


    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @SecurityRequirement(name = "")
    @Transactional
    @Operation(
        summary = "Create a new user",
        description = "Creates a new user with the provided details."
    )
    @Override
    public Response createUser(@Valid ReqCreateUserDto userDto) {
        return userService.createUser(userDto)
                .fold(
                    error -> {
                        ErrorResponse errorResponse = new ErrorResponse(
                            "User Creation Failed",
                            error.message(),
                            Response.Status.INTERNAL_SERVER_ERROR
                        );
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(errorResponse)
                                .build();
                    },
                    user -> {
                        ResEntryUserDto responseDto = userMapper.toDto(user);
                        SuccessResponse<ResEntryUserDto> successResponse = new SuccessResponse<>(
                            "User created successfully",
                            responseDto
                        );
                        return Response.status(Response.Status.CREATED)
                                .entity(successResponse)
                                .build();
                    }
                );
    } // end of createUser



    @PUT
    @Path("/change-email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
        summary = "Change user email",
        description = "Changes the email of the user with the provided details."
    )
    @Override
    public Response changeEmail(@Valid ReqChangeEmailDto changeEmailDto) {
        UUID userId = getCurrentUserIdOrThrow();
        
        return userService.changeEmail(changeEmailDto, userId)
                .fold(
                    error -> {
                        ErrorResponse errorResponse = new ErrorResponse(
                            "Email Change Failed",
                            error.message(),
                            Response.Status.INTERNAL_SERVER_ERROR
                        );
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(errorResponse)
                                .build();
                    },
                    user -> {
                        ResEntryUserDto responseDto = userMapper.toDto(user);
                        SuccessResponse<ResEntryUserDto> successResponse = new SuccessResponse<>(
                            "Email changed successfully",
                            responseDto
                        );
                        return Response.ok(successResponse).build();
                    }
                );
    } // end of changeEmail


    @PUT
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
        summary = "Change user password",
        description = "Changes the password of the user with the provided details."
    )
    @Override
    public Response changePassword(@Valid ReqChangePasswordDto changePasswordDto) {
        UUID userId = getCurrentUserIdOrThrow();
        
        return userService.changePassword(changePasswordDto, userId)
                .fold(
                    error -> {
                        ErrorResponse errorResponse = new ErrorResponse(
                            "Password Change Failed",
                            error.message(),
                            Response.Status.INTERNAL_SERVER_ERROR
                        );
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(errorResponse)
                                .build();
                    },
                    user -> {
                        ResEntryUserDto responseDto = userMapper.toDto(user);
                        SuccessResponse<ResEntryUserDto> successResponse = new SuccessResponse<>(
                            "Password changed successfully",
                            responseDto
                        );
                        return Response.ok(successResponse).build();
                    }
                );
    }


    @PUT
    @Path("/change-user-info")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(
        summary = "Change user information",
        description = "Changes the user information with the provided details."
    )
    @Override
    public Response changeUserInfo(@Valid ReqChangeUserInfoDto changeUserInfoDto) {
        UUID userId = getCurrentUserIdOrThrow();
        
        return userService.changeUserInfo(changeUserInfoDto, userId)
                .fold(
                    error -> {
                        ErrorResponse errorResponse = new ErrorResponse(
                            "User Info Change Failed",
                            error.message(),
                            Response.Status.INTERNAL_SERVER_ERROR
                        );
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(errorResponse)
                                .build();
                    },
                    user -> {
                        ResEntryUserDto responseDto = userMapper.toDto(user);
                        SuccessResponse<ResEntryUserDto> successResponse = new SuccessResponse<>(
                            "User info changed successfully",
                            responseDto
                        );
                        return Response.ok(successResponse).build();
                    }
                );
    }



    @DELETE
    @Transactional
    @Operation(
        summary = "Delete user by ID",
        description = "Deletes a user by their ID."
    )
    @Override
    public Response deleteUserById() {
        UUID userId = getCurrentUserIdOrThrow();
        
        return userService.deleteUserById(userId)
                .fold(
                    error -> {
                        ErrorResponse errorResponse = new ErrorResponse(
                            "User Deletion Failed",
                            error.message(),
                            Response.Status.INTERNAL_SERVER_ERROR
                        );
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                .entity(errorResponse)
                                .build();
                    },
                    success -> Response.status(Response.Status.NO_CONTENT).build()
                );
    }
    
}
