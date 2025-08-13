package common.repository.declare;

import com.spencerwi.either.Either;
import common.domain.entity.FileDetail;
import common.errorStructure.RepositoryError;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;

import java.util.List;
import java.util.UUID;

public interface FileAssetManagementRepository {

    Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase);

}
