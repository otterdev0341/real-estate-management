package sale.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.SaleTransaction;
import common.errorStructure.ServiceError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import contact.service.declare.DeclareContactService;
import fileDetail.service.declare.DeclareFileDetailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import property.service.declare.DeclarePropertyService;
import sale.domain.dto.ReqCreateSaleDto;
import sale.domain.dto.ReqUpdateSaleDto;
import sale.repository.internal.InternalSaleRepository;
import sale.service.internal.InternalSaleTransactionService;
import transaction.entity.choice.TransactionChoice;
import transaction.service.declare.DeclareTransactionService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;


@ApplicationScoped
@Named("saleTransactionService")
public class SaleTransactionServiceImpl implements InternalSaleTransactionService, FileAssetManagementService {

    private final DeclareUserService userService;
    private final InternalSaleRepository saleRepository;
    private final FileAssetManagementRepository fileAssetManagementRepository;
    private final DeclareContactService contactService;
    private final DeclarePropertyService propertyService;
    private final DeclareFileDetailService fileDetailService;
    private final DeclareTransactionService transactionService;

    @Inject
    public SaleTransactionServiceImpl(
            DeclareUserService userService,
            @Named("saleTransactionRepository") InternalSaleRepository saleRepository,
            @Named("saleTransactionRepository") FileAssetManagementRepository fileAssetManagementRepository,
            DeclareContactService contactService,
            @Named("propertyService") DeclarePropertyService propertyService,
            DeclareFileDetailService fileDetailService,
            DeclareTransactionService transactionService
    ) {
        this.userService = userService;
        this.saleRepository = saleRepository;
        this.fileAssetManagementRepository = fileAssetManagementRepository;
        this.contactService = contactService;
        this.propertyService = propertyService;
        this.fileDetailService = fileDetailService;
        this.transactionService = transactionService;
    }

    @Override
    public Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId, FileUpload targetFile) {
        // find saleTransaction
        // upload file
        // attach to sale transaction
        if (targetFile == null) return Either.left(new ServiceError.ValidationFailed("File to upload can't be null or empty"));
        return saleRepository.findSaleTransactionByIdAndUserId(targetId, userId)
                .mapLeft(saleTransactionError -> (ServiceError) new ServiceError.NotFound("Sale Transaction not found:" + saleTransactionError.message()))
                .flatMapRight(foundedSaleTransaction -> {
                    return fileDetailService.createFileDetail(targetFile, userId)
                            .mapLeft(uploadError -> uploadError)
                            .flatMapRight(uploadSuccess -> {
                                foundedSaleTransaction.addFileDetail(uploadSuccess);
                                return Either.right(true);
                            });
                });
    }

    @Override
    public Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId, UUID userId, UUID fileId) {
        // check is sale transaction exist
        // check is file exist
        // remove if it sale transaction contains file
        return saleRepository.findSaleTransactionByIdAndUserId(targetId, userId)
                .mapLeft(saleTransactionError -> (ServiceError) new ServiceError.OperationFailed("Failed to check is sale-transaction exist" + saleTransactionError.message()))
                .flatMapRight(foundedSaleTransaction -> {
                   return fileDetailService.findFileDetailAndUserId(fileId, userId)
                           .mapRight(foundedFileDetail -> Pair.of(foundedSaleTransaction, foundedFileDetail))
                           .mapLeft(fileDetailError -> fileDetailError);
                })
                .flatMapRight(pair -> {
                    SaleTransaction saleTransaction = pair.getLeft();
                    FileDetail fileDetail = pair.getRight();
                    if (!saleTransaction.getFileDetails().contains(fileDetail)) {
                        return Either.right(false);
                    }
                    saleTransaction.removeFileDetail(fileDetail);
                    return Either.right(true);
                });
    }

    @Override
    public Either<ServiceError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        return fileAssetManagementRepository.getAllFileByCriteria(targetId, userId, fileCase)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Failed to fetch all file related with query case: " + fileCase + error.message()));
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, SaleTransaction> createNewSaleTransaction(ReqCreateSaleDto reqCreateSaleDto, UUID userId) {
        // find Property : get object
        // find Contact : get contact
        // call GetTransactionPrePersist :: get Transaction
        // create PrePersistSaleTransaction
        // upload all fine attach to SaleTransaction
        // persist new Sale Transaction
        return propertyService.findPropertyByIdAndUserId(reqCreateSaleDto.getPropertyId(), userId)
                .mapLeft(propertyError -> propertyError)
                .flatMapRight(existProperty -> {
                    return contactService.findContactByIdAndUser(reqCreateSaleDto.getContactId(), userId)
                            .mapRight(existContact -> Pair.of(existProperty, existContact))
                            .mapLeft(contactError -> contactError);

                })
                .flatMapRight(pair -> {
                    return transactionService.getTransactionPrePersist(TransactionChoice.sale, userId, reqCreateSaleDto.getNote())
                            .mapRight(transaction -> {
                                SaleTransaction saleTransaction;
                                return saleTransaction = SaleTransaction
                                        .builder()
                                        .contact(pair.getRight())
                                        .property(pair.getLeft())
                                        .transaction(transaction)
                                        .price(reqCreateSaleDto.getPrice())
                                        .fileDetails(new HashSet<>())
                                        .build();

                            })
                            .mapLeft(transactionError -> transactionError);
                })
                .flatMapRight(preTransaction -> {
                    List<FileUpload> files = reqCreateSaleDto.getFiles();

                    // Check for null files first
                    if (files.stream().anyMatch(Objects::isNull)) {
                        return Either.left(new ServiceError.ValidationFailed("File to upload can't be null or empty"));
                    }

                    // Stream and map each file to its upload result (Either)
                    List<Either<ServiceError, FileDetail>> fileUploadResults = files.stream()
                            .map(eachFile -> fileDetailService.helpPrePersistFileDetail(eachFile, userId))
                            .toList();

                    // Process the list of Either to handle errors and populate the transaction
                    for (Either<ServiceError, FileDetail> result : fileUploadResults) {
                        if (result.isLeft()) {
                            return Either.left(result.getLeft());
                        }
                        preTransaction.addFileDetail(result.getRight());
                    }

                    return Either.right(preTransaction);
                })
                .flatMapRight(targetToPersist -> {
                    return saleRepository.createNewSaleTransaction(targetToPersist)
                            .mapRight(success -> success)
                            .mapLeft(saleError -> (ServiceError) new ServiceError.OperationFailed("Failed to create new sale transaction causes by" + saleError.message()));
                });
    }

    @Override
    public Either<ServiceError, SaleTransaction> updateSaleTransaction(ReqUpdateSaleDto reqUpdateSaleDto, UUID saleTransactionId, UUID userId) {
        // find sale transaction
        // find contact : get object
        // find property : get object
        // create preSale Transaction : update note
        // persist update
        return saleRepository.findSaleTransactionByIdAndUserId(saleTransactionId, userId)
                .mapLeft(findError -> (ServiceError) new ServiceError.OperationFailed("Failed to check is sale-transaction exist" + findError.message()))
                .flatMapRight(saleTransaction -> {
                    return contactService.findContactByIdAndUser(reqUpdateSaleDto.getContactId(), userId)
                            .mapRight(foundedContact -> Pair.of(saleTransaction, foundedContact))
                            .mapLeft(contactError -> contactError);
                })
                .flatMapRight(pair -> {
                    return propertyService.findPropertyByIdAndUserId(reqUpdateSaleDto.getPropertyId(), userId)
                            .mapRight(foundedProperty -> {
                                SaleTransaction preUpdatedSaleTransaction = pair.getLeft();
                                preUpdatedSaleTransaction.setContact(pair.getRight());
                                preUpdatedSaleTransaction.getTransaction().setNote(reqUpdateSaleDto.getNote());
                                preUpdatedSaleTransaction.setPrice(reqUpdateSaleDto.getPrice());
                                preUpdatedSaleTransaction.setProperty(foundedProperty);
                                return preUpdatedSaleTransaction;
                            })
                            .mapLeft(propertyError -> propertyError);
                })
                .flatMapRight(prePersistSaleTransaction -> {
                    return saleRepository.updateSaleTransaction(prePersistSaleTransaction)
                            .mapRight(updatedSuccess -> updatedSuccess)
                            .mapLeft(updateError -> (ServiceError) new ServiceError.PersistenceFailed("Fail to update sale-transaction cause by:" + updateError.message()));
                });
    }

    @Override
    public Either<ServiceError, SaleTransaction> findSaleTransactionByIdWithUserId(UUID transactionId, UUID userId) {
        return saleRepository.findSaleTransactionByIdAndUserId(transactionId, userId)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Error occurred while fetching sale transaction, cause by: " + error.message()));
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteSaleTransaction(UUID saleTransactionId, UUID userId) {
        return saleRepository.deleteSaleTransactionByIdAndUserId(saleTransactionId, userId)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Error occurred while delete sale transaction, cause by: " + error.message()));
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<SaleTransaction>> findAllSaleTransactionWithUserId(UUID userId, BaseQuery query) {
        return saleRepository.findAllSaleTransactionsWithUserId(userId, query)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Error occurred while fetching all sale transaction, cause by: " + error.message()));
                        },
                        Either::right
                );
    }
}
