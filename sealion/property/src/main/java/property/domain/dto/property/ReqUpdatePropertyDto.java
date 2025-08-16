package property.domain.dto.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePropertyDto {

    private String name;

    private String description;

    private String specific;

    private String highlight;

    private String area;

    private BigDecimal price;

    private BigDecimal fsp;

    private UUID propertyStatus;

    private UUID ownerBy;

    private String mapUrl;

    private String lat;

    private String lng;
}
