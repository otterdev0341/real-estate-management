package contact.domain.dto.contact;


import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateContactDto {
    @NotBlank(message = "Contact name cannot be blank")
    private String businessName;

    private String internalName;

    private String detail;

    private String note;

    @NotNull(message = "Contact type cannot be blank")
    private UUID contactType;

    private String address;

    private String phone;

    private String mobilePhone;

    private String line;

    private String email;
}
