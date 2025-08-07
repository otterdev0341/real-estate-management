package auth.service.internal;

import java.util.UUID;

import com.spencerwi.either.Either;
import common.domain.dto.auth.ReqLoginDto;
import common.domain.dto.auth.ResTokenDto;
import common.domain.entity.User;
import common.errorStructure.ServiceError;





public interface InternalAuthService {

    Either<ServiceError, ResTokenDto> login(ReqLoginDto loginDto);

    Either<ServiceError, User> resMe(UUID userId);
}
