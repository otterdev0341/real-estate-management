package common.controller.base;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class UnauthorizedException extends WebApplicationException {
    public UnauthorizedException(String message) {
        super(Response.status(Response.Status.UNAUTHORIZED)
                      .entity(message)
                      .build());
    }
}
