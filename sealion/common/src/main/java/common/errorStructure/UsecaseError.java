package common.errorStructure;

public sealed interface UsecaseError extends BaseError
    permits UsecaseError.InvalidRequest,
            UsecaseError.Unauthorized,
            UsecaseError.BusinessError,
            UsecaseError.NotFound,
            UsecaseError.Forbidden {

    record InvalidRequest(String message) implements UsecaseError {
        @Override
        public String code() { return "USE_001"; }
        @Override
        public ErrorType type() { return ErrorType.VALIDATION; }
    }

    record Unauthorized(String message) implements UsecaseError {
        @Override
        public String code() { return "USE_002"; }
        @Override
        public ErrorType type() { return ErrorType.UNAUTHORIZED; }
    }

    record BusinessError(String message) implements UsecaseError {
        @Override
        public String code() { return "USE_003"; }
        @Override
        public ErrorType type() { return ErrorType.BUSINESS_RULE; }
    }

    record NotFound(String message) implements UsecaseError {
        @Override
        public String code() { return "USE_004"; }
        @Override
        public ErrorType type() { return ErrorType.NOT_FOUND; }
    }

    record Forbidden(String message) implements UsecaseError {
        @Override
        public String code() { return "USE_005"; }
        @Override
        public ErrorType type() { return ErrorType.FORBIDDEN; }
    }
}