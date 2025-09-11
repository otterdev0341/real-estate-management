package property.domain.dto.property;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreatePropertyDto {

    private String name;

    private String description;

    private String specific;

    private String highlight;

    private String area;

    private BigDecimal price;

    private BigDecimal fsp;

    private BigDecimal Budget;

    private UUID propertyStatus;

    private UUID ownerBy;

    private String mapUrl;

    private String lat;

    private String lng;

    private List<FileUpload> files;
}
