package common.errorStructure;

public sealed interface RepositoryError extends BaseError 
    permits RepositoryError.NotFound, 
            RepositoryError.PersistenceFailed, 
            RepositoryError.FetchFailed,
            RepositoryError.DeleteFailed,
            RepositoryError.UpdateFailed,
            RepositoryError.PersistenceError{

    record NotFound(String message) implements RepositoryError {
        @Override
        public String code() { return "REPO_001"; }
        @Override
        public ErrorType type() { return ErrorType.NOT_FOUND; }
    }

    record PersistenceFailed(String message) implements RepositoryError {
        @Override
        public String code() { return "REPO_002"; }
        @Override
        public ErrorType type() { return ErrorType.PERSISTENCE; }
    }

    record FetchFailed(String message) implements RepositoryError {
        @Override
        public String code() { return "REPO_003"; }
        @Override
        public ErrorType type() { return ErrorType.TECHNICAL; }
    }

    record DeleteFailed(String message) implements RepositoryError {
        @Override
        public String code() { return "REPO_004"; }
        @Override
        public ErrorType type() { return ErrorType.PERSISTENCE; }
    }

    record UpdateFailed(String message) implements RepositoryError {
        @Override
        public String code() { return "REPO_005"; }
        @Override
        public ErrorType type() { return ErrorType.PERSISTENCE; }
    }

    record PersistenceError(String message) implements RepositoryError {
        @Override
        public String code() { return "REPO_006"; }
        @Override
        public ErrorType type() { return ErrorType.PERSISTENCE; }
    }
}
