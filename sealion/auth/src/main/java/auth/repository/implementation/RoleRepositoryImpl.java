package auth.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.entity.Role;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import auth.repository.internal.InternalRoleRepository;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RoleRepositoryImpl implements InternalRoleRepository, PanacheRepositoryBase<Role, UUID> {

    @Override
    public Either<RepositoryError, Optional<Role>> findUserRole() {
        try {
            Optional<Role> role = find("detail", "user").firstResultOptional();
            return Either.right(role);
        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to find user role"));
        }
    }

    @Override
    public Either<RepositoryError, Role> createRole(Role role) {
        try {
            persist(role);
            return Either.right(role);
        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to create user role"));
        }
    }

}
