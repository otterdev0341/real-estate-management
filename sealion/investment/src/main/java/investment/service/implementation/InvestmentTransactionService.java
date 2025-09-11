package investment.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.*;
import common.domain.entity.investment.InvestmentItem;
import common.domain.entity.investment.InvestmentTransaction;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import contact.service.declare.DeclareContactService;
import fileDetail.service.implementation.FileDetailService;
import investment.domain.dto.sub.InvestmentItemDto;
import investment.domain.dto.wrapper.ReqCreateInvestmentWrapperForm;
import investment.domain.dto.wrapper.ReqUpdateInvestmentWrapper;
import investment.repository.internal.InternalInvestmentTransactionRepository;
import investment.service.internal.InternalInvestmentTransactionService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import property.service.declare.DeclarePropertyService;
import transaction.entity.choice.TransactionChoice;
import transaction.service.implementation.TransactionService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Named("investmentService")
public class InvestmentTransactionService implements InternalInvestmentTransactionService, FileAssetManagementService {

    private final InternalInvestmentTransactionRepository investmentRepository;
    private final FileAssetManagementRepository fileAssetManagementRepository;
    private final FileDetailService fileDetailService;
    private final DeclareUserService userService;
    private final TransactionService transactionService;
    private final DeclarePropertyService propertyService;
    private final DeclareContactService contactService;



    @Inject
    public InvestmentTransactionService(
            @Named("investmentRepository") InternalInvestmentTransactionRepository investmentRepository,
            @Named("investmentRepository") FileAssetManagementRepository fileAssetManagementRepository,
            FileDetailService fileDetailService,
            DeclareUserService userService,
            TransactionService transactionService,
            DeclarePropertyService propertyService,
            DeclareContactService contactService

    ) {
        this.investmentRepository = investmentRepository;
        this.fileAssetManagementRepository = fileAssetManagementRepository;
        this.fileDetailService = fileDetailService;
        this.userService = userService;
        this.transactionService = transactionService;
        this.propertyService = propertyService;
        this.contactService = contactService;
    }

    @Override
    public Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId, FileUpload targetFile) {
        // find investment transaction
        // call file detail to create pre persist file detail
        // update investment transaction with file detail
        if (targetFile == null) return Either.left(new ServiceError.ValidationFailed("File to upload can't be null or empty"));
        return investmentRepository.findInvestmentTransactionByIdAndUserId(targetId, userId)
                .mapLeft(investmentTransactionError -> (ServiceError) new ServiceError.NotFound("Investment Transaction not found:" + investmentTransactionError.message()))
                .flatMapRight(foundedInvestmentTransaction -> {
                    return fileDetailService.helpPrePersistFileDetail(targetFile, userId)
                            .mapLeft(uploadError -> uploadError)
                            .flatMapRight(uploadSuccess -> {
                                foundedInvestmentTransaction.addFileDetail(uploadSuccess);
                                return Either.right(true);
                            });
                });
    }

    @Override
    public Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId, UUID userId, UUID fileId) {
        // check is investment transaction exist
        // check is file exist
        // remove if it investment transaction contains file
        return investmentRepository.findInvestmentTransactionByIdAndUserId(targetId, userId)
                .mapLeft(paymentTransactionError -> (ServiceError) new ServiceError.OperationFailed("Failed to check is investment-transaction exist" + paymentTransactionError.message()))
                .flatMapRight(foundedPaymentTransaction -> {
                    return fileDetailService.findFileDetailAndUserId(fileId, userId)
                            .mapRight(foundedFileDetail -> Pair.of(foundedPaymentTransaction, foundedFileDetail))
                            .mapLeft(fileDetailError -> fileDetailError);
                })
                .flatMapRight(pair -> {
                    InvestmentTransaction investmentTransaction = pair.getLeft();
                    FileDetail fileDetail = pair.getRight();
                    if (!investmentTransaction.getFileDetails().contains(fileDetail)) {
                        return Either.right(false);
                    }
                    investmentTransaction.removeFileDetail(fileDetail);
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
    public Either<ServiceError, InvestmentTransaction> createNewInvestmentTransaction(ReqCreateInvestmentWrapperForm reqCreateInvestmentWrapperForm, UUID userId) {
        // 1: create new investment transaction
        Either<ServiceError, InvestmentTransaction> investmentTransactionCase = helpCreateNewInvestmentTransaction(reqCreateInvestmentWrapperForm, userId);
        if (investmentTransactionCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to create investment transaction cause by :" + investmentTransactionCase.getLeft().message()));
        InvestmentTransaction targetInvestmentTransaction = investmentTransactionCase.getRight();
        targetInvestmentTransaction.setInvestmentDate(reqCreateInvestmentWrapperForm.getData().getPersistInvestmentDate());

        // 2: add investment transaction item
        Either<ServiceError, List<InvestmentItem>> investmentItemCase = helpCreateInvestmentTransactionItems(reqCreateInvestmentWrapperForm.getData().getItems(), userId);
        if (investmentItemCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to retrieved investment transaction items, cause by:" + investmentItemCase.getLeft().message()));
        List<InvestmentItem> targetItemList = investmentItemCase.getRight();
        for (InvestmentItem eachItem : targetItemList) {
            targetInvestmentTransaction.addInvestmentItem(eachItem);
        }

        // 3: loop through add file details and attach them to investment transaction
        Either<ServiceError, List<FileDetail>> fileDetailCase = helpUploadFileDetails(reqCreateInvestmentWrapperForm.getFiles(), userId);
        if (fileDetailCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed retrieved file detail list, cause by:" + fileDetailCase.getLeft().message()));
        List<FileDetail> targetFileDetail = fileDetailCase.getRight();
        for (FileDetail eachFileDetail : targetFileDetail) {
            targetInvestmentTransaction.addFileDetail(eachFileDetail);
        }
        return investmentRepository.createNewInvestmentTransaction(targetInvestmentTransaction)
                .fold(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Failed to create new investment transaction, cause by: " + error.message()));
                        }
                        , Either::right
                );
    }

    private Either<ServiceError, InvestmentTransaction> helpCreateNewInvestmentTransaction(
            ReqCreateInvestmentWrapperForm reqCreateInvestmentWrapperForm,
            UUID userId
    ) {
        // find user : get user object
        Either<ServiceError, User> userEither = userService.findUserById(userId);
        if (userEither.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find user, cause by:" + userEither.getLeft().message()));
        User user = userEither.getRight();

        // find property : get property object
        Either<ServiceError, Property> propertyEither = propertyService.findPropertyByIdAndUserId(reqCreateInvestmentWrapperForm.getData().getProperty(), userId);
        if (propertyEither.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find property, cause by" + propertyEither.getLeft().message()));
        Property property = propertyEither.getRight();
        // find investment transaction
        Either<ServiceError, Transaction> transactionCase = transactionService.getTransactionPrePersist(TransactionChoice.investment, userId, reqCreateInvestmentWrapperForm.getData().getNote());
        if (transactionCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to retrieved investment transaction"));
        Transaction investmentTransaction = transactionCase.getRight();

        // create investment transaction object
        return Either.right(
                InvestmentTransaction.builder()
                        .property(property)
                        .transaction(investmentTransaction)
                        .investmentItems(new ArrayList<>())
                        .fileDetails(new HashSet<>())
                        .build()
        );
    }

    private Either<ServiceError, List<InvestmentItem>> helpCreateInvestmentTransactionItems(
            List<InvestmentItemDto> transactionItemList,
            UUID userId
    ) {
        List<InvestmentItem> itemList = new ArrayList<>();
        // check is contact exist
        for (InvestmentItemDto item : transactionItemList) {
            Either<ServiceError, Contact> contactEither = contactService.findContactByIdAndUser(item.getContact(), userId);
            if (contactEither.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find contact" + contactEither.getLeft().message()));
            Contact contact = contactEither.getRight();
            InvestmentItem tempInvestmentItem = InvestmentItem.builder()
                    .contact(contact)
                    .amount(item.getAmount())
                    .percent(item.getPercent())
                    .build();
            itemList.add(tempInvestmentItem);
        }
        return  Either.right(itemList);
    }

    private Either<ServiceError, List<FileDetail>> helpUploadFileDetails(List<FileUpload> fileUploads, UUID userId) {
        // loop through file uploads
        // call file detail service to create file detail
        // add file detail to payment transaction
        List<FileDetail> fileDetailsList = new ArrayList<>();
        for (FileUpload fileUpload : fileUploads) {
            if (fileUpload == null) return Either.left(new ServiceError.ValidationFailed("File to upload can't be null or empty"));
            Either<ServiceError, FileDetail> uploadFileCase = fileDetailService.helpPrePersistFileDetail(fileUpload, userId);
            if (uploadFileCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to upload file, cause by: " + uploadFileCase.getLeft().message()));
            FileDetail fileDetail = uploadFileCase.getRight();
            fileDetailsList.add(fileDetail);
        }
        return Either.right(fileDetailsList);
    }

    @Override
    public Either<ServiceError, InvestmentTransaction> updateInvestmentTransaction(ReqUpdateInvestmentWrapper reqUpdateInvestmentWrapper, UUID investmentTransactionId, UUID userId) {
        // find investment transaction by ID and User id
        Either<RepositoryError, InvestmentTransaction> investmentTransactionCase = investmentRepository.findInvestmentTransactionByIdAndUserId(investmentTransactionId, userId);
        if (investmentTransactionCase.isLeft()) return Either.left(new ServiceError.NotFound("Investment transaction not found, cause by:" + investmentTransactionCase.getLeft().message()));
        InvestmentTransaction targetInvestmentTransaction = investmentTransactionCase.getRight();

        // update investment transaction info
        Either<ServiceError, InvestmentTransaction> updateTransactionInfoCase = helpUpdateInvestmentTransactionInfo(targetInvestmentTransaction, reqUpdateInvestmentWrapper, userId);
        if (updateTransactionInfoCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update, in process of updating investment transaction info, cause by:" + updateTransactionInfoCase.getLeft().message()));
        targetInvestmentTransaction = updateTransactionInfoCase.getRight();

        // update investment transaction item
        Either<ServiceError, InvestmentTransaction> paymentTransactionItemCase = helpUpdateInvestmentTransactionItems(targetInvestmentTransaction, reqUpdateInvestmentWrapper.getData().getItems(), userId);
        if (paymentTransactionItemCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update investment transaction item, cause by" + paymentTransactionItemCase.getLeft().message()));
        targetInvestmentTransaction = paymentTransactionItemCase.getRight();

        return investmentRepository.updateInvestmentTransaction(targetInvestmentTransaction)
                .fold(
                        error -> Either.left(new ServiceError.PersistenceFailed("Failed to update investment transaction, cause by: " + error.message())),
                        Either::right
                );
    }

    private Either<ServiceError, InvestmentTransaction> helpUpdateInvestmentTransactionInfo(
            InvestmentTransaction investmentTransaction,
            ReqUpdateInvestmentWrapper reqUpdateInvestmentWrapper,
            UUID userId
    ) {
        // if old property not the same as updated contact
        if (!investmentTransaction.getProperty().getId().equals(reqUpdateInvestmentWrapper.getData().getProperty())) {
            Either<ServiceError, Property> propertyEither = propertyService.findPropertyByIdAndUserId(reqUpdateInvestmentWrapper.getData().getProperty(), userId);
            if (propertyEither.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find property cause by:" + propertyEither.getLeft().message()));
            Property property = propertyEither.getRight();
            investmentTransaction.setProperty(property);
        }
        // if old created not the same as update created
        if (reqUpdateInvestmentWrapper.getData() != null) {
            investmentTransaction.setInvestmentDate(reqUpdateInvestmentWrapper.getData().getPersistInvestmentDate());
        }
        // if old note the same as update created
        investmentTransaction.getTransaction().setNote(reqUpdateInvestmentWrapper.getData().getNote());

        return Either.right(investmentTransaction);
    }

    private Either<ServiceError, InvestmentTransaction> helpUpdateInvestmentTransactionItems(
            InvestmentTransaction investmentTransaction,
            List<InvestmentItemDto> investmentItemDtoList,
            UUID userId
    ) {
        List<InvestmentItem> updatedItems = new ArrayList<>();

        for (InvestmentItemDto itemDto : investmentItemDtoList) {
            if (itemDto.getId() == null) {
                // Case 1: New item, create it
                Either<ServiceError, InvestmentItem> newItemCase = helpUpdateInvestmentItemCaseNullId(itemDto, userId);
                if (newItemCase.isLeft()) {
                    return Either.left(new ServiceError.OperationFailed("Failed to create new investment item: " + newItemCase.getLeft().message()));
                }
                updatedItems.add(newItemCase.getRight());
            } else {
                // Case 2: Existing item, find and update it
                Either<ServiceError, InvestmentItem> updatedItemCase = helpUpdateInvestmentItemCaseIdExist(itemDto, investmentTransaction, userId);
                if (updatedItemCase.isLeft()) {
                    return Either.left(new ServiceError.OperationFailed("Failed to update investment item with ID " + itemDto.getId() + ": " + updatedItemCase.getLeft().message()));
                }
                // The helpUpdateInvestmentItemCaseIdExist method already modifies the item in the transaction's list.
                // We just need to find it again to add to our `updatedItems` list.
                investmentTransaction.getInvestmentItems().stream()
                        .filter(item -> item.getId().equals(itemDto.getId()))
                        .findFirst()
                        .ifPresent(updatedItems::add);
            }
        }

        // Use the entity's method to clear the collection and correctly handle orphan removal.
        investmentTransaction.removeAllInvestmentItems();

        // Add the updated and new items back.
        // Hibernate will correctly manage INSERTs, UPDATEs, and DELETEs.
        for (InvestmentItem item : updatedItems) {
            investmentTransaction.addInvestmentItem(item);
        }

        return Either.right(investmentTransaction);
    }

    private Either<ServiceError, InvestmentItem> helpUpdateInvestmentItemCaseNullId(
            InvestmentItemDto investmentItemDto,
            UUID userId
    ){
        Either<ServiceError, Contact> contactCaseExist = contactService.findContactByIdAndUser(investmentItemDto.getContact(), userId);
        if (contactCaseExist.isLeft()) return Either.left(new ServiceError.ValidationFailed("Failed to fetch contact, cause by:" + contactCaseExist.getLeft().message()));

        Contact contact = contactCaseExist.getRight();
        InvestmentItem investmentItem = InvestmentItem.builder()
                .contact(contact)
                .amount(investmentItemDto.getAmount())
                .percent(investmentItemDto.getPercent())
                .build();
        return Either.right(investmentItem);
    }

    private Either<ServiceError, InvestmentItem> helpUpdateInvestmentItemCaseIdExist(
            InvestmentItemDto investmentItemDto,
            InvestmentTransaction investmentTransaction,
            UUID userId
    ){
        // Find the investment item by ID within the transaction's existing items.
        for (InvestmentItem investmentItem : investmentTransaction.getInvestmentItems()) {
            if (investmentItem.getId().equals(investmentItemDto.getId())) {
                // The item exists, now update its fields.

                // Check if the contact needs to be updated.
                Either<ServiceError, Contact> contactExistCase = contactService.findContactByIdAndUser(investmentItemDto.getContact(), userId);
                if (contactExistCase.isLeft()) {
                    return Either.left(new ServiceError.OperationFailed("Failed to find Contact with ID: , cause by: " + contactExistCase.getLeft().message()));
                }
                Contact contact = contactExistCase.getRight();
                investmentItem.setContact(contact);
                investmentItem.setAmount(investmentItemDto.getAmount());
                investmentItem.setPercent(investmentItemDto.getPercent());
                return Either.right(investmentItem);
            }
        }
        return Either.left(new ServiceError.NotFound("Investment item with ID " + investmentItemDto.getId() + " not found in the transaction."));
    }


    @Override
    public Either<ServiceError, InvestmentTransaction> findInvestmentTransactionByIdAndUserId(UUID investmentTransactionId, UUID userId) {
        return investmentRepository.findInvestmentTransactionByIdAndUserId(investmentTransactionId, userId)
                .fold(
                        error -> Either.left(new ServiceError.NotFound("Error occurred while fetching data due to: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteInvestmentTransactionById(UUID investmentTransactionId, UUID userId) {
        return investmentRepository.deleteInvestmentTransactionById(investmentTransactionId, userId)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Error occurred while deleting data due to: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<InvestmentTransaction>> findAllInvestmentTransactionWithUserId(UUID userId, BaseQuery query) {
        return investmentRepository.findAllInvestmentTransactionWIthUserId(userId, query)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Error occurred while fetching data due to: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<InvestmentTransaction>> findAllInvestmentByPropertyId(UUID propertyId, UUID userId) {
        return propertyService.findPropertyByIdAndUserId(propertyId, userId)
                .mapLeft(propertyNotFoundError -> propertyNotFoundError)
                .flatMapRight(property -> {
                    return investmentRepository.findAllInvestmentTransactionWIthUserId(userId, new BaseQuery())
                            .mapRight(items -> Pair.of(property, items))
                            .mapLeft(error -> new ServiceError.OperationFailed("Error occurred while fetching data due to: " + error.message()));
                })
                .flatMapRight(target -> {
                    Property targetProperty = target.getLeft();
                    List<InvestmentTransaction> targetList = target.getRight();
                    List<InvestmentTransaction> finalResult = targetList.stream().filter(item -> item.getProperty().getId().equals(propertyId)).toList();
                    return Either.right(finalResult);
                });
    }
}
