package property.domain.dto.propertyStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePropertyStatusDto {

    @NotBlank(message = "Detail must not be empty")
    private String detail;

}
