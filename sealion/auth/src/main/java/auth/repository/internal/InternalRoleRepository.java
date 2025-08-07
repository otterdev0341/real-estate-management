package auth.repository.internal;


import com.spencerwi.either.Either;
import common.domain.entity.Role;
import common.errorStructure.RepositoryError;

import java.util.Optional;

public interface InternalRoleRepository {
    
    Either<RepositoryError, Optional<Role>> findUserRole();

}
