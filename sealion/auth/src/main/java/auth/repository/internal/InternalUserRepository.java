package auth.repository.internal;


import com.spencerwi.either.Either;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;

import java.util.Optional;
import java.util.UUID;

public interface InternalUserRepository {
	
    // is email exist
    Either<RepositoryError, Boolean> existsByEmail(String email);

    // is username exist
    Either<RepositoryError, Boolean> existsByUsername(String username);

    Either<RepositoryError, Optional<User>> findByEmail(String email);

    Either<RepositoryError, User> findUserById(UUID userId);

    // create new user
    Either<RepositoryError, User> createUser(User user);

    // update exist user
    Either<RepositoryError, User> updateUser(User user);

    // delete user by id
    Either<RepositoryError, Boolean> deleteUserById(UUID userId);


}
