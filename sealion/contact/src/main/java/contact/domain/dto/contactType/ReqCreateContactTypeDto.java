package contact.domain.dto.contactType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateContactTypeDto {
    @NotBlank(message = "Contact type cannot be blank")
    private String detail;
}
