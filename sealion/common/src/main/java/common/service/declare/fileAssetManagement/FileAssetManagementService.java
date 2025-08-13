package common.service.declare.fileAssetManagement;

import com.spencerwi.either.Either;
import common.domain.entity.FileDetail;
import common.errorStructure.ServiceError;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;
import java.util.UUID;
/*
*  to use getAllFileByCriteria The Entity must implement HasFileDetails
*  make sure that entity must have relation many to many with fileDetail
* */
public interface  FileAssetManagementService  {
    // upload single file
    Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId ,FileUpload targetFile);
    // delete by target and fileId
    Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId, UUID userId ,UUID fileId);
    // master: get file related by case
    Either<ServiceError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase);

}
