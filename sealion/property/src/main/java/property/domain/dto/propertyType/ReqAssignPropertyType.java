package property.domain.dto.propertyType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqAssignPropertyType {

    @NotNull(message = "property type for assign to property can not be null")
    List<UUID> propertyTypes;

}
