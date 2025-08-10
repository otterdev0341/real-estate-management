package auth.repository.implementation;


import com.spencerwi.either.Either;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import auth.repository.internal.InternalUserRepository;
import org.hibernate.HibernateError;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserRepositoryImpl implements PanacheRepositoryBase<User, UUID>, InternalUserRepository {



    @Override
    public Either<RepositoryError, Boolean> existsByEmail(String email) {
        try {
            boolean exists = find("email", email).firstResultOptional().isPresent();
            return Either.right(exists);
        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to check if email exists"));
        }
    }
    

    @Override
    public Either<RepositoryError, Boolean> existsByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return Either.left(new RepositoryError.FetchFailed("Username cannot be null or empty"));
        }
        try {
            boolean exists = find("username", username).count() > 0;
            return Either.right(exists);
        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to check if username exists: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Optional<User>> findByEmail(String email) {
        try {
            Optional<User> user = find("email", email).firstResultOptional();
            return Either.right(user);
        } catch (HibernateError he) {
            return Either.left(new RepositoryError.NotFound("Failed to find user by email due hibernate error"));
        }
        catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to find user by email(Repository)"));
        }
    }

    @Override
    public Either<RepositoryError, User> findUserById(UUID userId) {
        try {
            Optional<User> user = find("id", userId).firstResultOptional();
            return user
                    .<Either<RepositoryError, User>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Failed to find user by ID")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find user by ID"));
        }
    }

       

    @Override
    public Either<RepositoryError, User> createUser(User user) {
        try {
            persist(user);
            return Either.right(user);
        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to create user"));
        }
    }

    @Override
    public Either<RepositoryError, User> updateUser(User user) {
        try {
            User mergedUser = getEntityManager().merge(user);
            return Either.right(mergedUser);
        } catch (Exception e) {
            // logger.error("Error updating user", e);
            return Either.left(new RepositoryError.PersistenceError("Failed to update user: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteUserById(UUID userId) {
        try {
            User user = findById(userId);
            if (user == null) {
                return Either.left(new RepositoryError.NotFound("User not found"));
            }
            delete(user);
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to delete user by ID"));
        }
    }

    
    
}
