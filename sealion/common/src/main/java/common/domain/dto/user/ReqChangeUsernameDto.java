package common.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqChangeUsernameDto {
    
    @NotBlank(message = "Current username is required")
    private String newUsername;

    @NotBlank(message = "Password is required")
    private String password;
}
