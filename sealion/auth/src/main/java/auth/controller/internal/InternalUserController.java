package auth.controller.internal;


import common.domain.dto.user.ReqChangeEmailDto;
import common.domain.dto.user.ReqChangePasswordDto;
import common.domain.dto.user.ReqChangeUserInfoDto;
import common.domain.dto.user.ReqCreateUserDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

public interface InternalUserController {
    
    
    Response createUser(@Valid ReqCreateUserDto userDto);

    Response changeEmail(@Valid ReqChangeEmailDto changeEmailDto);

    Response changePassword(@Valid ReqChangePasswordDto changePasswordDto);

    Response changeUserInfo(@Valid ReqChangeUserInfoDto changeUserInfoDto);

    Response deleteUserById();
}
