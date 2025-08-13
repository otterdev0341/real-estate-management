package property.domain.dto.property;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ReqUpdatePropertyDto {

    @NotBlank(message = "Name must not be empty")
    private String name;

    private String description;

    private String specific;

    private String highlight;

    private String area;

    private BigDecimal price;

    private BigDecimal fsp;

    @NotNull(message = "Property status must not be empty")
    private UUID propertyStatus;

    @NotNull(message = "OwnerBy must not be empty")
    private UUID ownerBy;

    private String mapUrl;

    private String lat;

    private String lng;
}
