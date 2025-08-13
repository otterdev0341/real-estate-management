package property.domain.dto.propertyType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqUpdatePropertyTypeDto {
    @NotBlank(message = "Detail must not be empty")
    private String detail;
}
