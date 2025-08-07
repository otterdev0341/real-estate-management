package auth.controller.implementation;


import java.util.UUID;

import auth.domain.dto.TestDto;
import common.controller.base.BaseController;
import auth.controller.internal.InternalAuthController;
import auth.service.internal.InternalAuthService;
import common.domain.dto.auth.ReqLoginDto;
import common.domain.dto.auth.ResTokenDto;
import common.domain.dto.user.ResEntryUserDto;
import common.domain.mapper.UserMapper;
import common.response.SuccessResponse;
import io.quarkus.logging.Log;
import jakarta.annotation.security.PermitAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.security.*;



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
@Path("/auth")
public class AuthController extends BaseController implements InternalAuthController {


    private final InternalAuthService authService;
    private final UserMapper userMapper;
    


    @Inject
    public AuthController(InternalAuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
        
    }


    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    @Operation(
        summary = "User Login",
        description = "Authenticates a user and returns a JWT token."
    )
    @Override
    public Response login(@Valid ReqLoginDto reqLoginDto) {



        return authService.login(reqLoginDto)
                .fold(
                    error -> Response.status(Response.Status.UNAUTHORIZED)
                        .entity(error.message())
                        .build(),
                    success -> Response.ok(new SuccessResponse<ResTokenDto>("login success", success)).build()
                );
    }


    @POST
    @Path("/resme")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Get user information",
        description = "Retrieves the information of the currently authenticated user."
    )
    @Override
    public Response resMe() {
    
        UUID userId = getCurrentUserIdOrThrow();


    return authService.resMe(userId)
            .fold(
                error -> Response.status(Response.Status.UNAUTHORIZED)
                    .entity(error.message())
                    .build(),
                success -> {
                    ResEntryUserDto responseDto = userMapper.toDto(success);
                    return Response.ok(new SuccessResponse<>("resMe success", responseDto)).build();
                }
            );
    }

    @POST
    @Path("/test")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response Test(@Valid TestDto testDto) {
        SuccessResponse<TestDto> theResponse = new SuccessResponse<>(
                "test success",
                testDto
        );
        return Response.ok(theResponse).build();
    }
    
}
