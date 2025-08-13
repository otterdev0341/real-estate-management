package property.domain.dto.propertyStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqUpdatePropertyStatusDto {

    @NotBlank(message = "Detail must not be empty")
    private String detail;

}
