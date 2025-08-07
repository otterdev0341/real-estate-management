package common.controller.base;

import java.util.Optional;
import java.util.UUID;

import common.service.implementation.JwtService;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

public abstract class BaseController {
    
    @Inject
    protected JwtService jwtService;

    protected UUID getCurrentUserIdOrThrow() {
        return jwtService.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User ID not found in SecurityIdentity"));
    }

    protected Optional<UUID> getOptionalCurrentUserId() {
        return jwtService.getCurrentUserId();
    }

    protected Response unauthorized(String message) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(message)
                .build();
    }

}
