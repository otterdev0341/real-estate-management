package common.domain.dto.fileDetail;


import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAttachFile {

    @RestForm("file") // You can still name the form part "file"
    @NotNull(message = "File must not be null")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    // Change this to a List to match the working implementation
    private List<FileUpload> file;
}


