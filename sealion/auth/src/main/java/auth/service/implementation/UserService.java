package auth.service.implementation;


import auth.repository.internal.InternalGenderRepository;
import auth.repository.internal.InternalRoleRepository;
import auth.repository.internal.InternalUserRepository;
import auth.service.internal.InternalUserService;
import com.spencerwi.either.Either;
import common.domain.dto.user.*;
import common.domain.entity.Gender;
import common.domain.entity.Role;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import auth.service.declare.DeclareUserService;
import org.mindrot.jbcrypt.BCrypt;


import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
class UserService implements InternalUserService, DeclareUserService {

    private final InternalGenderRepository genderRepository;
    private final InternalRoleRepository roleRepository;
    private final InternalUserRepository userRepository;


    @Inject
    public UserService(InternalGenderRepository genderRepository, InternalRoleRepository roleRepository, InternalUserRepository userRepository) {
        this.genderRepository = genderRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }





    @Override
    public Either<ServiceError, Boolean> existsById(UUID userId) {
        return userRepository.findUserById(userId)
                .fold(
                    error -> {
                        return Either.left(new ServiceError.OperationFailed("Unexpected Error occurred: " + userId));
                    }, 
                    success -> success.isPresent() ? Either.right(true) : Either.right(false)
                );
    }

    @Override
    public Either<ServiceError, Optional<User>> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .fold(
                    error -> Either.left(new ServiceError.OperationFailed("Unexpected Error occurred (userService findByEmail): " + email + "cause by" + error.message())),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, Optional<User>> findUserById(UUID userId) {
        return userRepository.findUserById(userId)
                .fold(
                    error -> Either.left(new ServiceError.OperationFailed("Unexpected Error occurred (user Service): " + userId)),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> existsByEmail(String email) {
        return userRepository.existsByEmail(email)
                .fold(
                    error -> Either.left(new ServiceError.OperationFailed("Unexpected Error occurred (exist by email): " + email)),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> existsByUsername(String username) {
        return userRepository.existsByUsername(username)
                .fold(
                    error -> Either.left(new ServiceError.OperationFailed("Unexpected Error occurred: " + username)),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, User> createUser(ReqCreateUserDto userDto) {
        
        Either<ServiceError, User> validatedUser = validateNewUser(userDto);
        if (validatedUser.isLeft()) {
            return Either.left(validatedUser.getLeft());
        }
        
        User newUser = validatedUser.getRight();
        return userRepository.createUser(newUser)
                .fold(
                    error -> Either.left(new ServiceError.PersistenceFailed("Unexpected Error occurred while creating user: " + newUser.getUsername())),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, User> changeEmail(ReqChangeEmailDto changeEmailDto, UUID userId) {
        // check is userId exist
        Either<RepositoryError, Optional<User>> user = userRepository.findUserById(userId);
        if (user.isLeft() || user.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("User not found: " + userId));
        }

        // check is email exist and not belong to user
        Either<RepositoryError, Boolean> isEmailExist = userRepository.existsByEmail(changeEmailDto.getNewEmail().trim());
        if (isEmailExist.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Unexpected Error occurred while checking email existence: " + changeEmailDto.getNewEmail()));
        }
        if (isEmailExist.getRight() == true) {
            return Either.left(new ServiceError.DuplicateEntry("Email already exists: " + changeEmailDto.getNewEmail()));
        }
        if (user.getRight().get().getEmail().equals(changeEmailDto.getNewEmail().trim())) {
            return Either.left(new ServiceError.DuplicateEntry("New email is same as current email: " + changeEmailDto.getNewEmail()));
        }
        // check is password in dto match with user password
        if (!BCrypt.checkpw(changeEmailDto.getPassword(), user.getRight().get().getPassword())) {
            return Either.left(new ServiceError.ValidationFailed("Invalid password provided for user: " + userId));
        }
        // update user email
        User userToUpdate = user.getRight().get();
        userToUpdate.setEmail(changeEmailDto.getNewEmail().trim());
        
        // persist update only email field
        return userRepository.updateUser(userToUpdate)
                .fold(
                    error -> Either.left(new ServiceError.PersistenceFailed("Unexpected Error occurred while updating user email: " + userToUpdate.getUsername())),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, User> changePassword(ReqChangePasswordDto changePasswordDto, UUID userId) {
        // check is userId exist
        Either<RepositoryError, Optional<User>> user = userRepository.findUserById(userId);
        if (user.isLeft() || user.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("User not found: " + userId));
        }
        // check is old password match with user password
        if (!BCrypt.checkpw(changePasswordDto.getOldPassword(), user.getRight().get().getPassword())) {
            return Either.left(new ServiceError.ValidationFailed("Invalid old password provided for user: " + userId));
        }
        // check is new password and verify new password match
        if (!changePasswordDto.getNewPassword().equals(changePasswordDto.getVerifyNewPassword())) {
            return Either.left(new ServiceError.ValidationFailed("New password and confirm password do not match for user: " + userId));
        }
        // hash new password
        String hashedNewPassword = BCrypt.hashpw(changePasswordDto.getNewPassword(), BCrypt.gensalt());
        // update password field
        User userToUpdate = user.getRight().get();
        userToUpdate.setPassword(hashedNewPassword);
        // perform update
        return userRepository.updateUser(userToUpdate)
                .fold(
                    error -> Either.left(new ServiceError.PersistenceFailed("Unexpected Error occurred while updating user password: " + userToUpdate.getUsername())),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, User> changeUserInfo(ReqChangeUserInfoDto changeUserInfoDto, UUID userId) {
        // check is user exist,
        Either<RepositoryError, Optional<User>> user = userRepository.findUserById(userId);
        if (user.isLeft() || user.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("User not found: " + userId));
        }
        User userToUpdate = user.getRight().get();
        // check is gender exist
        Either<RepositoryError, Optional<Gender>> gender = genderRepository.findByDetail(changeUserInfoDto.getGender());
        if (gender.isLeft() || gender.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("Gender not found: " + changeUserInfoDto.getGender()));
        }
        // update field
        userToUpdate.setFirstName(changeUserInfoDto.getFirstName().trim());
        userToUpdate.setLastName(changeUserInfoDto.getLastName().trim());
        userToUpdate.setDob(changeUserInfoDto.getDob());
        userToUpdate.setGender(gender.getRight().get());

        return userRepository.updateUser(userToUpdate)
                .fold(
                    error -> Either.left(new ServiceError.PersistenceFailed("Unexpected Error occurred while updating user info: " + userToUpdate.getUsername())),
                    Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteUserById(UUID userId) {
        // check is user exist
        Either<RepositoryError, Optional<User>> user = userRepository.findUserById(userId);
        if (user.isLeft() || user.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("User not found: " + userId));
        }
        return userRepository.deleteUserById(userId)
                .fold(
                    error -> Either.left(new ServiceError.PersistenceFailed("Unexpected Error occurred while deleting user: " + userId)),
                    Either::right
                );
    }


    public Either<ServiceError, User> validateNewUser(ReqCreateUserDto userDto) {
        // check is username exist
        Either<RepositoryError, Boolean> isUsernameExist = userRepository.existsByUsername(userDto.getUsername().trim());
        if (isUsernameExist.isLeft() || isUsernameExist.getRight() == true) {
            return Either.left(new ServiceError.DuplicateEntry("Validation Error occurred in service " + isUsernameExist.getLeft().message()));
        }

        // check is email exist
        Either<RepositoryError, Boolean> isEmailExist = userRepository.existsByEmail(userDto.getEmail().trim());
        if (isEmailExist.isLeft()) {
            return Either.left(new ServiceError.DuplicateEntry("Unexpected Error occurred email already exist"));
        }
        // hash password
        String hashedPassword = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
        // get gender object
        Either<RepositoryError, Optional<Gender>> gender = genderRepository.findByDetail(userDto.getGender());
        if (gender.isLeft() || gender.getRight().isEmpty()) {
            return Either.left(new ServiceError.OperationFailed("Unexpected Error occurred while fetching gender detail: " + userDto.getGender()));
        }
        // get user role object
        Either<RepositoryError, Optional<Role>> role = roleRepository.findUserRole();
        if (role.isLeft() || role.getRight().isEmpty()) {
            return Either.left(new ServiceError.OperationFailed("Unexpected Error occurred while fetching user role"));
        }

        User newUser = new User();
        newUser.setUsername(userDto.getUsername().trim());
        newUser.setEmail(userDto.getEmail().trim());
        newUser.setPassword(hashedPassword);
        newUser.setGender(gender.getRight().get());
        newUser.setRole(role.getRight().get());
        newUser.setFirstName(userDto.getFirstName().trim());
        newUser.setLastName(userDto.getLastName().trim());
        newUser.setDob(userDto.getDob());

        return Either.right(newUser);
        
    }

    
} // end class
