package auth.service.internal;

import java.util.Optional;
import com.spencerwi.either.Either;
import common.domain.entity.Gender;
import common.errorStructure.ServiceError;

public interface InternalGenderService {
    Either<ServiceError, Optional<Gender>> findByDetail(String detail);
}
