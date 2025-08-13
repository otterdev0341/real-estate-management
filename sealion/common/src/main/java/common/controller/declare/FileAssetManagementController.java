package common.controller.declare;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.UUID;

public interface FileAssetManagementController {

    Response attachFileToTarget(UUID targetId, FileUpload targetFile);

    Response deleteFileFromTarget(UUID targetId, UUID targetFileId);

    Response getAllFileByCriteria(UUID targetId, @NotBlank(message = "file case is required") String fileCase);

}
