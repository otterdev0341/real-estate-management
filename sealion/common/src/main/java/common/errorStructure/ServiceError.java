package common.errorStructure;

public sealed interface ServiceError extends BaseError 
    permits ServiceError.ValidationFailed,
            ServiceError.BusinessRuleFailed,
            ServiceError.OperationFailed,
            ServiceError.DuplicateEntry,
            ServiceError.PersistenceFailed,
            ServiceError.NotFound {

    record ValidationFailed(String message) implements ServiceError {
        @Override
        public String code() { return "SVC_001"; }
        @Override
        public ErrorType type() { return ErrorType.VALIDATION; }
    }

    record BusinessRuleFailed(String message) implements ServiceError {
        @Override
        public String code() { return "SVC_002"; }
        @Override
        public ErrorType type() { return ErrorType.BUSINESS_RULE; }
    }

    record OperationFailed(String message) implements ServiceError {
        @Override
        public String code() { return "SVC_003"; }
        @Override
        public ErrorType type() { return ErrorType.TECHNICAL; }
    }

    record DuplicateEntry(String message) implements ServiceError {
        @Override
        public String code() { return "SVC_004"; }
        @Override
        public ErrorType type() { return ErrorType.VALIDATION; }
    }

    record NotFound(String message) implements ServiceError {
        @Override
        public String code() { return "SVC_005"; }
        @Override
        public ErrorType type() { return ErrorType.NOT_FOUND; }
    }

    record PersistenceFailed(String message) implements ServiceError {
        @Override
        public String code() { return "SVC_006"; }
        @Override
        public ErrorType type() { return ErrorType.TECHNICAL; }
    }
}
