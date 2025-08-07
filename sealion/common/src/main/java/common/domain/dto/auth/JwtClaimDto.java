package common.domain.dto.auth;

import java.util.Set;
import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtClaimDto {
    private String subject;
    private Set<String> groups;
    private long expiresInMillis;
}
