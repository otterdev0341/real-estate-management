package memo.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.Memo;
import common.domain.entity.MemoType;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import fileDetail.service.declare.DeclareFileDetailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import memo.domain.dto.memo.ReqCreateMemoDto;
import memo.domain.dto.memo.ReqUpdateMemoDto;
import memo.repository.internal.InternalMemoRepository;
import memo.repository.internal.InternalMemoTypeRepository;
import memo.service.declare.DeclareMemoService;
import memo.service.internal.InternalMemoService;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@ApplicationScoped
@Named("memoService")
public class MemoService implements DeclareMemoService, InternalMemoService, FileAssetManagementService {

    private final InternalMemoRepository memoRepository;
    private final InternalMemoTypeRepository memoTypeRepository;
    private final DeclareUserService userService;
    private final DeclareFileDetailService fileDetailService;
    private final FileAssetManagementRepository fileAssetManagementRepository;

    @Inject
    public MemoService(
            @Named("memoRepository") InternalMemoRepository memoRepository,
            @Named("memoRepository") FileAssetManagementRepository fileAssetManagementRepository,
            InternalMemoTypeRepository memoTypeRepository,
            DeclareUserService userService,
            DeclareFileDetailService fileDetailService
    ) {
        this.memoRepository = memoRepository;
        this.memoTypeRepository = memoTypeRepository;
        this.userService = userService;
        this.fileDetailService = fileDetailService;
        this.fileAssetManagementRepository = fileAssetManagementRepository;
    }

    // file asset implementation
    @Override
    public Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId, FileUpload targetFile) {
        // find memo target
        // update file
        // attached to memo
        if (targetFile == null) {
            ServiceError theError = new ServiceError.ValidationFailed("File can't be null to perform attach file operation");
            return Either.left(theError);
        }

        return memoRepository.findMemoAndUserId(targetId, userId)
                .flatMap(error -> {
                   ServiceError theError = new ServiceError.NotFound(error.message());
                   return Either.left(theError);
                },
                memo -> {
                    return fileDetailService.createFileDetail(targetFile, userId)
                            .flatMap(
                                    Either::left,
                            fileDetail -> {
                                memo.addFileDetail(fileDetail);
                                return memoRepository.updateMemo(memo)
                                        .flatMap(error -> {
                                            ServiceError theError = new ServiceError.OperationFailed("can't update file cause by: " + error.message());
                                            return Either.left(theError);
                                        }, successOperation -> Either.right(true));
                            });
                });


    }

    @Override
    public Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId,UUID userId, UUID fileId) {
        // check is memo exist
        // check is file exist
        // perform delete
        return memoRepository.findMemoAndUserId(targetId, userId)
                .flatMap(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Memo not found" + error.message());
                            return Either.left(theError);
                        },
                        memo -> {
                            return fileDetailService.findFileDetailAndUserId(fileId, userId)
                                    .flatMap(
                                            Either::left,
                                            fileDetail -> {
                                                boolean isMemoContainFile = memo.getFileDetails().contains(fileDetail);
                                                if (!isMemoContainFile) {
                                                    ServiceError theError = new ServiceError.BusinessRuleFailed("File does not belong to memo");
                                                    return Either.left(theError);
                                                }
                                                memo.removeFileDetail(fileDetail);

                                                return memoRepository.updateMemo(memo)
                                                        .flatMap(
                                                                error -> {
                                                                    ServiceError theError = new ServiceError.PersistenceFailed("Can't update memo cause by: " + error.message());
                                                                    return Either.left(theError);
                                                                },
                                                                success -> Either.right(true)
                                                        );
                                            }
                                    );
                        }
                );
    }

    @Override
    public Either<ServiceError, List<FileDetail>> getAllFileByCriteria(UUID targetId,UUID userId, FileCaseSelect fileCase) {
        return fileAssetManagementRepository.getAllFileByCriteria(targetId, userId, fileCase)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to retrive files related by memo reason by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    // declare and internal implementation
    @Override
    public Either<ServiceError, Boolean> isExistByIdAndUserId(UUID memoId, UUID userId) {
        return memoRepository.isExistByIdAndUserId(memoId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("fail to check memo is exist or not, cause by " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Memo> findMemoByIdAndUserId(UUID memoId, UUID userId) {
        return memoRepository.findMemoAndUserId(memoId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error on finding memo cause by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> isExistByNameAndUserId(String name, UUID userId) {
        return memoRepository.isExistByNameAndUserId(name.trim(), userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error on checking is memo name exist" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Memo> createNewMemo(ReqCreateMemoDto reqCreateMemo, UUID userId) {
        // find user object
        // check is new memo name is exist
        // check memo type is exist
        // prepare memo entity
        // persist new entity
        // handle upload file

        return userService.findUserById(userId)
                .flatMapLeft(Either::left)
                .flatMapRight(foundedUser -> {
                    return memoRepository.isExistByNameAndUserId(reqCreateMemo.getName(), userId)
                            .mapRight(isMemoNameExist -> Pair.of(foundedUser, isMemoNameExist))
                            .mapLeft(error -> new ServiceError.OperationFailed("Failed to check is memo name exist it database, casuse by: " + error.message()));

                })
                .flatMapRight(pair -> {
                    User user = pair.getLeft();
                    Boolean isNewMemoNameExist = pair.getRight();
                    if (isNewMemoNameExist) {
                        ServiceError theError = new ServiceError.DuplicateEntry("the new memo name already exist");
                        return Either.left(theError);
                    }
                    return memoTypeRepository.findMemoTypeAndUserId(reqCreateMemo.getMemoType(), userId)
                            .mapRight(memoType -> Pair.of(user, memoType))
                            .mapLeft(error -> new ServiceError.OperationFailed("Memo type that you try to use is not exist, can't create new Memo" + error.message()));
                })
                .flatMapRight(pair -> {
                    User user = pair.getLeft();
                    MemoType memoType = pair.getRight();
                    Memo memo = Memo.builder()
                            .name(reqCreateMemo.getName().trim())
                            .detail(reqCreateMemo.getDetail())
                            .memoType(memoType)
                            .createdBy(user)
                            .fileDetails(new HashSet<>())
                            .build();
                    return memoRepository.createMemo(memo)
                            .mapRight(persisted -> persisted)
                            .mapLeft(error -> new ServiceError.PersistenceFailed("Failed to persist new memo reason by: " + error.message()));

                })
                .flatMapRight(persistedMemo -> {
                    List<FileUpload> files = reqCreateMemo.getFiles();
                    if (files.isEmpty()) {
                        return Either.right(persistedMemo);
                    }

                    // Use a stream to process all files and link them to the memo
                    Either<ServiceError, Memo> result = Either.right(persistedMemo);

                    for (FileUpload file : files) {
                        result = result.flatMapRight(currentMemo ->
                                helperUploadFile(currentMemo, file)
                                        .mapRight(fileDetail -> {
                                            currentMemo.addFileDetail(fileDetail);
                                            return currentMemo;
                                        })
                        );
                    }

                    return result;

                });
    }

    @Override
    public Either<ServiceError, Memo> updateMemo(ReqUpdateMemoDto reqUpdateMemoDto, UUID userId, UUID memoId) {
        // check is memoType exist and belong to user
        // check is new memo name exist
        // check is memo type exist
        // perform update
        return memoRepository.findMemoAndUserId(memoId, userId)
                .flatMapLeft(error -> {
                    ServiceError theError = new ServiceError.OperationFailed("Failed to find memo by id cause by :" + error.message());
                    return Either.left(theError);
                })
                .flatMapRight(memo -> {
                    if(!memo.getCreatedBy().getId().equals(userId)) {
                        ServiceError theError = new ServiceError.NotFound("Memo not found or not belong to the user");
                        return Either.left(theError);
                    }
                    return memoRepository.isExistByNameAndUserId(reqUpdateMemoDto.getName(), userId)
                            .mapRight(isExist -> Pair.of(memo, isExist))
                            .mapLeft(error -> new ServiceError.OperationFailed("Failed to check new memo name to update cause by :" + error.message()));
                })
                .flatMapRight(pair -> {

                    if (pair.getRight()) {
                        ServiceError theError = new ServiceError.DuplicateEntry("the new memo name already exist");
                        return Either.left(theError);
                    }
                    return memoTypeRepository.findMemoTypeAndUserId(reqUpdateMemoDto.getMemoType(), userId)
                            .mapRight(memoType -> Pair.of(pair.getLeft(), memoType))
                            .mapLeft(error -> new ServiceError.OperationFailed("contact type not exist, cause by:" + error.message()));

                })
                .flatMapRight(pair -> {
                    Memo targetMemo = pair.getLeft();
                    MemoType memoType = pair.getRight();
                    targetMemo.setName(reqUpdateMemoDto.getName().trim());
                    targetMemo.setDetail(reqUpdateMemoDto.getDetail());
                    targetMemo.setMemoType(memoType);

                    return memoRepository.updateMemo(targetMemo)
                            .flatMapRight(Either::right)
                            .mapLeft(error -> new ServiceError.OperationFailed("Update memo error cause by:" + error.message()));

                });



    }

    @Override
    public Either<ServiceError, Boolean> deleteMemoByIdAndUserId(UUID memoId, UUID userId) {
        return helperDeleteMemoWithRelationFile(memoId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to delete memo cause by:" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<Memo>> findAllMemoWithUserId(UUID userId, BaseQuery query) {
        return memoRepository.findAllMemoWithUserId(userId, query)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to fetch memos cause by " + error.message());
                            return Either.left(theError);

                        },
                        Either::right
                );
    }


    private Either<ServiceError, FileDetail> helperUploadFile(Memo memo, FileUpload file) {
        // persisted to file detail
        Either<ServiceError, FileDetail> fileDetailPersisted = fileDetailService.createFileDetail(file, memo.getCreatedBy().getId());
        if (fileDetailPersisted.isLeft()) {
            return Either.left(fileDetailPersisted.getLeft());
        }
        return Either.right(fileDetailPersisted.getRight());

    }

    private Either<ServiceError, Boolean> helperDeleteMemoWithRelationFile(UUID memoId, UUID userId) {
        // 1. Find the memo and its associated files
        Either<RepositoryError, Memo> isMemoExist = memoRepository.findMemoAndUserId(memoId, userId);
        if(isMemoExist.isLeft()) {
            ServiceError theError = new ServiceError.NotFound("Fail to delete Failed on fetch operation: " + isMemoExist.getLeft().message());
            return Either.left(theError);
        }
        Memo memo = isMemoExist.getRight();

        // 2. Get the associated FileDetail entities.
        Set<FileDetail> filesToDelete = memo.getFileDetails();

        // 3. Delete the memo (this will clear the join table entries for this memo)
        Either<RepositoryError, Boolean> deletedMemoResult = memoRepository.deleteMemoByIdAndUserId(memo.getId(), userId);
        if(deletedMemoResult.isLeft()) {
            ServiceError theError = new ServiceError.OperationFailed("Error occurred while delete memo cause by" + deletedMemoResult.getLeft().message() );
            return Either.left(theError);
        }
        if (!deletedMemoResult.getRight()) {
            ServiceError theError = new ServiceError.PersistenceFailed("Failed to delete memo cause by" + deletedMemoResult.getLeft().message() );
            return Either.left(theError);
        }

        // 4. Check if a file is an orphan and delete it if so
        for (FileDetail file : filesToDelete) {
            Either<ServiceError, Boolean> deletedRelatedFileResult = fileDetailService.deleteFileDetailByFileIdAndUserId(file.getId(), userId);
            if (deletedRelatedFileResult.isLeft()) {
                ServiceError deleteRelationError = new ServiceError.OperationFailed("Error occurred by delete file associate with memo cause by: " + deletedRelatedFileResult.getLeft().message());
                return Either.left(deleteRelationError);
            }
            if(!deletedRelatedFileResult.getRight()) {
                ServiceError deleteRelationError = new ServiceError.BusinessRuleFailed("Failed to delete file associate with memo expect true to return but got : " + deletedRelatedFileResult.getRight());
                return Either.left(deleteRelationError);
            }
        }

        return Either.right(true);
    }
}
