package cloudFlare.service.declare;

import com.spencerwi.either.Either;
import common.domain.dto.fileDetail.ResFileR2Dto;
import common.domain.entity.FileDetail;
import common.errorStructure.ServiceError;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public interface DeclareCloudFlareR2Service {

    Either<ServiceError, ResFileR2Dto> uploadFile(FileUpload file);

    Either<ServiceError, ResFileR2Dto> getFile(String objectKey);

    Either<ServiceError, Boolean> deleteFile(String objectKey);

}
