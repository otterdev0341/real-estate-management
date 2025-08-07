package common.domain.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReqChangePasswordDto {
    
    @NotBlank(message = "New password is required")
    private String newPassword; 

    @NotBlank(message = "Verify new password is required")
    private String verifyNewPassword;

    @NotBlank(message = "Old password is required")
    private String oldPassword;

}
