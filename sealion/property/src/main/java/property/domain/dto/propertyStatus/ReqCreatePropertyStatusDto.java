package property.domain.dto.propertyStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqCreatePropertyStatusDto {

    @NotBlank(message = "Detail must not be empty")
    private String detail;

}
