package common.domain.dto.fileDetail;


import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAttachFile {

    @RestForm("file")
    @NotNull(message = "File must not be null")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    private FileUpload file;
}

