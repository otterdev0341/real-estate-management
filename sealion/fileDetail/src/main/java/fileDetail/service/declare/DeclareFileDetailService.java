package fileDetail.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.FileDetail;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.UUID;

public interface DeclareFileDetailService {

    Either<ServiceError, Boolean> isExistByFileIdAndUserId(UUID fileId, UUID userId);

    Either<ServiceError, FileDetail> createFileDetail(FileUpload fileDetail, UUID userId);

    Either<ServiceError, FileDetail> findFileDetailAndUserId(UUID fileId, UUID userId);

    Either<ServiceError, Boolean> deleteFileDetailByFileIdAndUserId(UUID fileDetailId, UUID userId);

    Either<ServiceError, FileDetail> helpPrePersistFileDetail(FileUpload fileUpload, UUID userId);
}
