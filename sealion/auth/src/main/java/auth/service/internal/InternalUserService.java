package auth.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.user.*;
import common.domain.entity.User;
import common.errorStructure.ServiceError;


import java.util.UUID;

public interface InternalUserService {
    
    Either<ServiceError, Boolean> existsByEmail(String email);

    Either<ServiceError, Boolean> existsByUsername(String username);

    Either<ServiceError, User> createUser(ReqCreateUserDto userDto);
    
    Either<ServiceError, User> changeEmail(ReqChangeEmailDto changeEmailDto, UUID userId);

    Either<ServiceError, User> changePassword(ReqChangePasswordDto changePasswordDto, UUID userId);

    Either<ServiceError, User> changeUserInfo(ReqChangeUserInfoDto changeUserInfoDto, UUID userId);

    Either<ServiceError, Boolean> deleteUserById(UUID userId);
    
}
