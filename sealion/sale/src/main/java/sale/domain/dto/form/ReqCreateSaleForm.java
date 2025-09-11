package sale.domain.dto.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateSaleForm {

    @RestForm("saleDate")
    @PartType(MediaType.TEXT_PLAIN)
    private String saleDate;

    @RestForm("note")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Note is required")
    private String note;

    @RestForm("propertyId")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Property is required")
    private String propertyId;

    @RestForm("contactId")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Contact is required")
    private String contactId;

    @RestForm("price")
    @PartType(MediaType.TEXT_PLAIN)
    @NotBlank(message = "Price cannot be blank")
    private String price;

    @RestForm("files")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private List<FileUpload> files;

}
