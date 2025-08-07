package common.response;

import common.errorStructure.UsecaseError;
import jakarta.ws.rs.core.Response;

public class ErrorResponse {
    private String error;
    private String message;
    private int statusCode;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.statusCode = 500; // Default to internal server error
    }

    public ErrorResponse(String error, String message, Response.Status status) {
        this.error = error;
        this.message = message;
        this.statusCode = status.getStatusCode();
    }

    public static ErrorResponse fromUsecaseError(UsecaseError error, String defaultMessage) {
        Response.Status status = mapErrorToStatus(error);
        return new ErrorResponse(error.message(), defaultMessage, status);
    }

    private static Response.Status mapErrorToStatus(UsecaseError error) {
        return switch (error) {
            case UsecaseError.InvalidRequest e -> Response.Status.BAD_REQUEST;
            case UsecaseError.Unauthorized e -> Response.Status.UNAUTHORIZED;
            case UsecaseError.NotFound e -> Response.Status.NOT_FOUND;
            case UsecaseError.Forbidden e -> Response.Status.FORBIDDEN;
            case UsecaseError.BusinessError e -> Response.Status.CONFLICT;
            default -> Response.Status.INTERNAL_SERVER_ERROR;
        };
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
