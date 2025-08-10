package auth.service.declare;


import com.spencerwi.either.Either;
import common.domain.entity.User;
import common.errorStructure.ServiceError;

import java.util.Optional;
import java.util.UUID;

public interface DeclareUserService {

    

    Either<ServiceError, Boolean> existsById(UUID userId);
    
    Either<ServiceError, Optional<User>> findByEmail(String email);

    Either<ServiceError, User> findUserById(UUID userId);
}
