package fileDetail.repository.internal;

import com.spencerwi.either.Either;
import common.domain.entity.FileType;
import common.errorStructure.RepositoryError;



public interface InternalFileTypeRepository {

    Either<RepositoryError, FileType> getFileTypeByFileWithExtension(String fileNameWithExtension);

     Either<RepositoryError, FileType> getPdfFileType();

    Either<RepositoryError, FileType> getImageFileType();

    Either<RepositoryError, FileType> getOtherFileType();

}
