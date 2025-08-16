package fileDetail.service.implementation;

import auth.service.declare.DeclareUserService;
import cloudFlare.service.declare.DeclareCloudFlareR2Service;
import com.spencerwi.either.Either;
import common.domain.dto.fileDetail.ResFileR2Dto;
import common.domain.entity.FileDetail;
import common.domain.entity.FileType;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import fileDetail.repository.internal.InternalFileDetailRepository;
import fileDetail.repository.internal.InternalFileTypeRepository;
import fileDetail.service.declare.DeclareFileDetailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.UUID;


@ApplicationScoped
public class FileDetailService implements DeclareFileDetailService {

    private final InternalFileDetailRepository fileDetailRepository;
    private final InternalFileTypeRepository fileTypeRepository;
    private final DeclareUserService userService;
    private final DeclareCloudFlareR2Service cloudFlareR2Service;

    @Inject
    public FileDetailService(InternalFileDetailRepository fileDetailRepository, InternalFileTypeRepository fileTypeRepository, DeclareUserService userService, DeclareCloudFlareR2Service cloudFlareR2Service) {
        this.fileDetailRepository = fileDetailRepository;
        this.fileTypeRepository = fileTypeRepository;
        this.userService = userService;
        this.cloudFlareR2Service = cloudFlareR2Service;
    }

    @Override
    public Either<ServiceError, Boolean> isExistByFileIdAndUserId(UUID fileId, UUID userId) {
        return fileDetailRepository.isExistByFileIdAndUserId(fileId, userId)
                .fold(
                        error -> {
                            ServiceError serviceError = new ServiceError.OperationFailed("Failed to check if file exists for user" + error.message());
                            return Either.left(serviceError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, FileDetail> createFileDetail(FileUpload fileDetail, UUID userId) {
        // check if user exists : if failed return error
        Either<ServiceError, User> userExist = userService.findUserById(userId);
        if (userExist.isLeft()) {
            return Either.left(new ServiceError.NotFound("User not found with id: " + userExist.getLeft().message()));
        }
        User user = userExist.getRight();

        // update file to cloud : if failed return error
        Either<ServiceError, ResFileR2Dto> uploadedFile = cloudFlareR2Service.uploadFile(fileDetail);
        if (uploadedFile.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to upload file to cloud: " + uploadedFile.getLeft().message()));
        }
        ResFileR2Dto resFileR2Dto = uploadedFile.getRight();

        // check file type : if failed return error
        Either<RepositoryError, FileType> fileType = fileTypeRepository.getFileTypeByFileWithExtension(resFileR2Dto.getFileName());
        if(fileType.isLeft()) {
            // delete file from cloud if file type not found
            Either<ServiceError, Boolean> deleteFile = cloudFlareR2Service.deleteFile(resFileR2Dto.getObjectKey());
            if (deleteFile.isLeft()) {
                return Either.left(new ServiceError.OperationFailed("Failed to delete file from cloud: " + deleteFile.getLeft().message()));
            }
            return Either.left(new ServiceError.NotFound("File type not found for extension: " + resFileR2Dto.getFileName()));
        }
        FileType type = fileType.getRight();
        // create file detail : if failed delete file from cloud
        FileDetail fileDetail1 = FileDetail.builder()
                .name(resFileR2Dto.getFileName())
                .objectKey(resFileR2Dto.getObjectKey())
                .path(resFileR2Dto.getFileUrl())
                .type(type)
                .size(resFileR2Dto.getContentLength())
                .createdBy(user)
                .build();

        // persist file detail
        Either<RepositoryError, FileDetail> persistFileDetail = fileDetailRepository.createFileDetail(fileDetail1);
        // if failed delete file from cloud and return error
        if (persistFileDetail.isLeft()) {
            // delete file from cloud if file detail not created
            Either<ServiceError, Boolean> deleteFile = cloudFlareR2Service.deleteFile(resFileR2Dto.getObjectKey());
            if (deleteFile.isLeft()) {
                return Either.left(new ServiceError.OperationFailed("Failed to delete file from cloud: " + deleteFile.getLeft().message()));
            }
            return Either.left(new ServiceError.OperationFailed("Failed to create file detail: " + persistFileDetail.getLeft().message()));
        }
        FileDetail createdFileDetail = persistFileDetail.getRight();
        return Either.right(createdFileDetail);

    }

    @Override
    public Either<ServiceError, FileDetail> findFileDetailAndUserId(UUID fileId, UUID userId) {
        return fileDetailRepository.findFileDetailAndUserId(fileId, userId)
                .fold(
                        error -> Either.left(new ServiceError.NotFound("File detail not found for fileId: " + fileId + ". Error: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteFileDetailByFileIdAndUserId(UUID fileDetailId, UUID userId) {
        // check if file detail exists : if failed return error
        Either<RepositoryError, FileDetail> fileDetailExist = fileDetailRepository.findFileDetailAndUserId(fileDetailId, userId);
        if (fileDetailExist.isLeft()) {
            return Either.left(new ServiceError.NotFound("File detail not found for fileId: " + fileDetailId + ". Error: " + fileDetailExist.getLeft().message()));
        }
        FileDetail fileDetail = fileDetailExist.getRight();

        // delete cloud first : if failed return error
        Either<ServiceError, Boolean> deleteFile = cloudFlareR2Service.deleteFile(fileDetail.getObjectKey());
        if (deleteFile.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to delete file from cloud: " + deleteFile.getLeft().message()));
        }
        if (!deleteFile.getRight()) {
            return Either.left(new ServiceError.OperationFailed("Failed to delete file from cloud, but file detail exists in database"));
        }

        // delete record in database : if failed return error
        Either<RepositoryError, Boolean> deleteFileDetail = fileDetailRepository.deleteFileDetailByFileIdAndUserId(fileDetailId, userId);
        if (deleteFileDetail.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to delete file detail: " + deleteFileDetail.getLeft().message()));
        }


        return Either.right(deleteFileDetail.getRight());
    }

    @Override
    public Either<ServiceError, FileDetail> helpPrePersistFileDetail(FileUpload fileUpload, UUID userId) {
        // check if user exists : if failed return error
        Either<ServiceError, User> userExist = userService.findUserById(userId);
        if (userExist.isLeft()) {
            return Either.left(new ServiceError.NotFound("User not found with id: " + userExist.getLeft().message()));
        }
        User user = userExist.getRight();

        // update file to cloud : if failed return error
        Either<ServiceError, ResFileR2Dto> uploadedFile = cloudFlareR2Service.uploadFile(fileUpload);
        if (uploadedFile.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to upload file to cloud: " + uploadedFile.getLeft().message()));
        }
        ResFileR2Dto resFileR2Dto = uploadedFile.getRight();

        // check file type : if failed return error
        Either<RepositoryError, FileType> fileType = fileTypeRepository.getFileTypeByFileWithExtension(resFileR2Dto.getFileName());
        if(fileType.isLeft()) {
            // delete file from cloud if file type not found
            Either<ServiceError, Boolean> deleteFile = cloudFlareR2Service.deleteFile(resFileR2Dto.getObjectKey());
            if (deleteFile.isLeft()) {
                return Either.left(new ServiceError.OperationFailed("Failed to delete file from cloud: " + deleteFile.getLeft().message()));
            }
            return Either.left(new ServiceError.NotFound("File type not found for extension: " + resFileR2Dto.getFileName()));
        }
        FileType type = fileType.getRight();
        // create file detail : if failed delete file from cloud
        FileDetail fileDetail1 = FileDetail.builder()
                .name(resFileR2Dto.getFileName())
                .objectKey(resFileR2Dto.getObjectKey())
                .path(resFileR2Dto.getFileUrl())
                .type(type)
                .size(resFileR2Dto.getContentLength())
                .createdBy(user)
                .build();


        return Either.right(fileDetail1);
    }
}
