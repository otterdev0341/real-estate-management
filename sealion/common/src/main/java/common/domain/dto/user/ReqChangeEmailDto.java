package common.domain.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqChangeEmailDto {
    
    @Email(message = "Invalid email format")
    @NotBlank(message = "Old email is required")
    private String oldEmail;

    @Email(message = "Invalid email format")
    @NotBlank(message = "New email is required")
    private String newEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
