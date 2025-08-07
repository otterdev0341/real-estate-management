package auth.service.internal;

import com.spencerwi.either.Either;
import common.domain.entity.Role;
import common.errorStructure.ServiceError;

import java.util.Optional;

public interface InternalRoleService {
    
    Either<ServiceError, Optional<Role>> findUserRole();

}
