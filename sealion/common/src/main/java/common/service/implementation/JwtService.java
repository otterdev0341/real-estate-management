package common.service.implementation;


import common.domain.dto.auth.JwtClaimDto;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;


import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class JwtService {
    
    @Inject
    SecurityIdentity securityIdentity;

    public String generateJwt(JwtClaimDto payload) {


//        Log.infov("Generated JWT for subject {0} with groups {1}: {2}",
//                payload.getSubject(),
//                payload.getGroups(),
//                token);

        return Jwt.issuer("sea-salt")
                .subject(payload.getSubject())
                .groups(payload.getGroups())
                .expiresIn(payload.getExpiresInMillis())
                .sign();
    }

    public Optional<UUID> getCurrentUserId() {
        if (securityIdentity == null || securityIdentity.getPrincipal() == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(UUID.fromString(securityIdentity.getPrincipal().getName()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
}
}
