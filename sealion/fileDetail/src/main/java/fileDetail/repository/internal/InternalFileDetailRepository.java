package fileDetail.repository.internal;

import com.spencerwi.either.Either;
import common.domain.entity.FileDetail;
import common.errorStructure.RepositoryError;
import java.util.UUID;



public interface InternalFileDetailRepository {

    Either<RepositoryError, Boolean> isExistByFileIdAndUserId(UUID fileId, UUID userId);

    Either<RepositoryError, FileDetail> createFileDetail(FileDetail fileDetail);

    Either<RepositoryError, FileDetail> findFileDetailAndUserId(UUID fileId, UUID userId);

    Either<RepositoryError, Boolean> deleteFileDetailByFileIdAndUserId(UUID fileId, UUID userId);

}
