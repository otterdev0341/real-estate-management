package auth.repository.internal;

import com.spencerwi.either.Either;
import common.domain.entity.Gender;
import common.errorStructure.RepositoryError;

import java.util.Optional;

public interface InternalGenderRepository {
    
    Either<RepositoryError, Optional<Gender>> findByDetail(String detail);

    Either<RepositoryError, Gender> createGender(Gender gender);
}
