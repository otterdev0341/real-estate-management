package cloudFlare.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.fileDetail.ResFileR2Dto;
import common.errorStructure.RepositoryError;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public interface InternalCloudFlareR2Repository {

    Either<RepositoryError, ResFileR2Dto> uploadFile(FileUpload file);

    Either<RepositoryError, ResFileR2Dto> getFile(String objectKey);

    Either<RepositoryError, Boolean> deleteFile(String objectKey);
}
