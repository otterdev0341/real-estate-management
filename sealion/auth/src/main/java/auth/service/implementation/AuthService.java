package auth.service.implementation;


import auth.service.internal.InternalAuthService;
import com.spencerwi.either.Either;
import common.domain.dto.auth.JwtClaimDto;
import common.domain.dto.auth.ReqLoginDto;
import common.domain.dto.auth.ResTokenDto;
import common.domain.entity.User;
import common.errorStructure.ServiceError;
import auth.service.declare.DeclareUserService;
import common.service.implementation.JwtService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@ApplicationScoped
public class AuthService implements InternalAuthService {

    private final JwtService jwtService;
    private final DeclareUserService userService;
    


    @Inject
    public AuthService(JwtService jwtService, DeclareUserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }


    @Override
    public Either<ServiceError, ResTokenDto> login(ReqLoginDto loginDto) {


        // get user by email
        Either<ServiceError, Optional<User>> userEither = userService.findByEmail(loginDto.getEmail().trim());
        if (userEither.isLeft()) {
            return Either.left(userEither.getLeft());
        }
        if (userEither.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("User not found with email: " + loginDto.getEmail()));
        }
        User theUser = userEither.getRight().get();

        // check if login password matches userEmail
        if(!BCrypt.checkpw(loginDto.getPassword(), theUser.getPassword())) {
            return Either.left(new ServiceError.ValidationFailed("Invalid credentials"));
        }
        
        // if correct generate jwt token
        JwtClaimDto theClaim = new JwtClaimDto();
        theClaim.setSubject(theUser.getId().toString());
        theClaim.setGroups(Set.of(theUser.getRole().getDetail()));
        theClaim.setExpiresInMillis(3600000L);
        
        String token = jwtService.generateJwt(theClaim);
        if (token == null) {
            return Either.left(new ServiceError.OperationFailed("Failed to generate JWT token"));
        }
        ResTokenDto resTokenDto = new ResTokenDto();
        resTokenDto.setToken(token);
        return Either.right(resTokenDto);
    }


    @Override
    public Either<ServiceError, User> resMe(UUID userId) {
        Either<ServiceError, Optional<User>> userEither = userService.findUserById(userId);
        if (userEither.isLeft()) {
            return Either.left(userEither.getLeft());
        }
        if (userEither.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("User not found with ID: " + userId));
        }
        User theUser = userEither.getRight().get();
        
        return Either.right(theUser);
    }
    
}
