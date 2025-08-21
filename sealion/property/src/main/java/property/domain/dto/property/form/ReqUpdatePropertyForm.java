package property.domain.dto.property.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePropertyForm {
    @RestForm("name")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Property name cannot be blank")
    private String name;

    @RestForm("description")
    @PartType(MediaType.TEXT_PLAIN)
    private String description;

    @RestForm("specific")
    @PartType(MediaType.TEXT_PLAIN)
    private String specific;

    @RestForm("highlight")
    @PartType(MediaType.TEXT_PLAIN)
    private String highlight;

    @RestForm("area")
    @PartType(MediaType.TEXT_PLAIN)
    private String area;

    @RestForm("price")
    @PartType(MediaType.TEXT_PLAIN)
    private String price;

    @RestForm("fsp")
    @PartType(MediaType.TEXT_PLAIN)
    private String fsp;

    @RestForm("propertyStatus")
    @PartType(MediaType.TEXT_PLAIN)
    private String propertyStatus;

    @RestForm("ownerBy")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "Owner by is required")
    private String ownerBy;

    @RestForm("mapUrl")
    @PartType(MediaType.TEXT_PLAIN)
    private String mapUrl;

    @RestForm("lat")
    @PartType(MediaType.TEXT_PLAIN)
    private String lat;

    @RestForm("lng")
    @PartType(MediaType.TEXT_PLAIN)
    private String lng;


}
