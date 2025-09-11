package sale.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateSaleDto {

    private LocalDateTime saleDate;

    private String note;

    private UUID propertyId;

    private UUID contactId;

    private BigDecimal price;

    private List<FileUpload> files;

}
