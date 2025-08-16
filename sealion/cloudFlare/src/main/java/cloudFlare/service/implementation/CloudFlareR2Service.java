package cloudFlare.service.implementation;

import cloudFlare.repository.internal.InternalCloudFlareR2Repository;
import cloudFlare.service.declare.DeclareCloudFlareR2Service;
import com.spencerwi.either.Either;
import common.domain.dto.fileDetail.ResFileR2Dto;
import common.domain.entity.FileDetail;
import common.errorStructure.ServiceError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.multipart.FileUpload;


@ApplicationScoped
public class CloudFlareR2Service implements DeclareCloudFlareR2Service {

    private final InternalCloudFlareR2Repository cloudFlareR2Repository;

    @Inject
    public CloudFlareR2Service(InternalCloudFlareR2Repository cloudFlareR2Repository) {
        this.cloudFlareR2Repository = cloudFlareR2Repository;
    }

    @Override
    public Either<ServiceError, ResFileR2Dto> uploadFile(FileUpload file) {
        return cloudFlareR2Repository.uploadFile(file)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to upload file to CloudFlare R2" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, ResFileR2Dto> getFile(String objectKey) {
        return cloudFlareR2Repository.getFile(objectKey)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to retrieve file from CloudFlare R2: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteFile(String objectKey) {
        return cloudFlareR2Repository.deleteFile(objectKey)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to delete file from CloudFlare R2: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }


}
