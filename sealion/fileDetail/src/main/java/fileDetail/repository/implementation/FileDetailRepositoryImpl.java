package fileDetail.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.entity.FileDetail;
import common.errorStructure.RepositoryError;
import fileDetail.repository.internal.InternalFileDetailRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;



@ApplicationScoped
public class FileDetailRepositoryImpl implements PanacheRepositoryBase<FileDetail, UUID>, InternalFileDetailRepository {

    @Override
    public Either<RepositoryError, Boolean> isExistByFileIdAndUserId(UUID fileId, UUID userId) {
        try {
            Optional<FileDetail> isExist = find("fileId = ?1 and userId = ?2", fileId, userId).firstResultOptional();
            if (isExist.isPresent()) {
                return Either.right(true);
            } else {
                return Either.right(false);
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence of FileDetail for fileId: " + fileId + " and userId: " + userId + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, FileDetail> createFileDetail(FileDetail fileDetail) {
        try {
            persist(fileDetail);
            return Either.right(fileDetail);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceError("Error creating FileDetail: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, FileDetail> findFileDetailAndUserId(UUID fileId, UUID userId) {
        try {
            Optional<FileDetail> fileDetail = find("fileId = ?1 and userId = ?2", fileId, userId).firstResultOptional();
            return fileDetail.<Either<RepositoryError, FileDetail>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.FetchFailed("FileDetail not found for fileId: " + fileId)));
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error fetching FileDetail for fileId: " + fileId + " cause by " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteFileDetailByFileIdAndUserId(UUID fileId, UUID userId) {
        try {
            Optional<FileDetail> fileDetail = find("fileId = ?1 and userId = ?2", fileId, userId).firstResultOptional();
            if (fileDetail.isPresent()) {
                delete(fileDetail.get());
                return Either.right(true);
            } else {
                return Either.right(false);
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.DeleteFailed("Error deleting FileDetail for fileId: " + fileId + " cause by :" + e.getMessage()));
        }
    }
}
