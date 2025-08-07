package auth.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.entity.Gender;
import common.errorStructure.RepositoryError;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import auth.repository.internal.InternalGenderRepository;

import java.util.Optional;
import java.util.UUID;



@ApplicationScoped
public class GenderRepositoryImpl implements InternalGenderRepository, PanacheRepositoryBase<Gender, UUID> {

    @Override
    public Either<RepositoryError, Optional<Gender>> findByDetail(String detail) {
        
        try{
            Optional<Gender> gender = find("detail", detail).firstResultOptional();
            return Either.right(gender);

        } catch (Exception e) {
            return Either.left(new RepositoryError.NotFound("Failed to find gender by detail"));
        }
        
    }

    
    
}
