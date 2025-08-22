package payment.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.*;
import common.domain.entity.payment.PaymentItem;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import contact.domain.dto.contact.ResEntryContactDto;
import contact.service.declare.DeclareContactService;
import expense.service.declare.DeclareExpenseService;
import fileDetail.service.implementation.FileDetailService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import payment.domain.dto.item.ReqCreatePaymentItemDto;
import payment.domain.dto.item.ReqUpdatePaymentItemDto;
import payment.domain.dto.wrapper.ReqCreatePaymentWrapperForm;
import payment.domain.dto.wrapper.ReqUpdatePaymentWrapperForm;
import payment.repository.internal.InternalPaymentTransactionRepository;
import payment.service.internal.InternalPaymentTransactionService;
import property.service.declare.DeclarePropertyService;
import transaction.entity.choice.TransactionChoice;
import transaction.service.implementation.TransactionService;

import java.util.*;


@ApplicationScoped
@Named("paymentTransactionService")
public class PaymentTransactionService implements InternalPaymentTransactionService, FileAssetManagementService {

    private final InternalPaymentTransactionRepository paymentTransactionRepository;
    private final FileAssetManagementRepository fileAssetManagementRepository;
    private final FileDetailService fileDetailService;
    private final DeclareUserService userService;
    private final DeclareContactService contactService;
    private final DeclarePropertyService propertyService;
    private final TransactionService transactionService;
    private final DeclareExpenseService expenseService;

    public PaymentTransactionService(
            @Named("paymentTransactionRepository") InternalPaymentTransactionRepository paymentTransactionRepository,
            @Named("paymentTransactionRepository") FileAssetManagementRepository fileAssetManagementRepository,
            FileDetailService fileDetailService,
            DeclareUserService userService,
            DeclareContactService contactService,
            DeclarePropertyService propertyService,
            TransactionService transactionService,
            DeclareExpenseService expenseService
    ) {
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.fileAssetManagementRepository = fileAssetManagementRepository;
        this.fileDetailService = fileDetailService;
        this.userService = userService;
        this.contactService = contactService;
        this.propertyService = propertyService;
        this.transactionService = transactionService;
        this.expenseService = expenseService;
    }

    @Override
    public Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId, FileUpload targetFile) {
        // find payment transaction
        // call file detail to create persistent file detail
        // update payment transaction with file detail
        if (targetFile == null) return Either.left(new ServiceError.ValidationFailed("File to upload can't be null or empty"));
        return paymentTransactionRepository.findPaymentTransactionByIdAndUserId(targetId, userId)
                .mapLeft(saleTransactionError -> (ServiceError) new ServiceError.NotFound("Payment Transaction not found:" + saleTransactionError.message()))
                .flatMapRight(foundedPaymentTransaction -> {
                    return fileDetailService.createFileDetail(targetFile, userId)
                            .mapLeft(uploadError -> uploadError)
                            .flatMapRight(uploadSuccess -> {
                                foundedPaymentTransaction.addFileDetail(uploadSuccess);
                                return Either.right(true);
                            });
                });
    }

    @Override
    public Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId, UUID userId, UUID fileId) {
        // check is payment transaction exist
        // check is file exist
        // remove if it payment transaction contains file
        return paymentTransactionRepository.findPaymentTransactionByIdAndUserId(targetId, userId)
                .mapLeft(paymentTransactionError -> (ServiceError) new ServiceError.OperationFailed("Failed to check is payment-transaction exist" + paymentTransactionError.message()))
                .flatMapRight(foundedPaymentTransaction -> {
                    return fileDetailService.findFileDetailAndUserId(fileId, userId)
                            .mapRight(foundedFileDetail -> Pair.of(foundedPaymentTransaction, foundedFileDetail))
                            .mapLeft(fileDetailError -> fileDetailError);
                })
                .flatMapRight(pair -> {
                    PaymentTransaction paymentTransaction = pair.getLeft();
                    FileDetail fileDetail = pair.getRight();
                    if (!paymentTransaction.getFileDetails().contains(fileDetail)) {
                        return Either.right(false);
                    }
                    paymentTransaction.removeFileDetail(fileDetail);
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
    public Either<ServiceError, PaymentTransaction> findPaymentTransactionByIdAndUserId(UUID paymentTransactionId, UUID userId) {
        return paymentTransactionRepository.findPaymentTransactionByIdAndUserId(paymentTransactionId, userId)
                .fold(
                        error -> Either.left(new ServiceError.NotFound("Payment transaction not found for ID: " + paymentTransactionId + " and user ID: " + userId + " due to: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> deletePaymentTransactionById(UUID paymentTransactionId, UUID userId) {
        return paymentTransactionRepository.deletePaymentTransactionById(paymentTransactionId, userId)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Failed to delete payment transaction: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<PaymentTransaction>> findAllPaymentTransactionWithUserId(UUID userId, BaseQuery query) {
        return paymentTransactionRepository.findAlPaymentTransactionWithUserId(userId, query)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Failed to fetch all payment transactions for user: " + userId + " due to: " + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, PaymentTransaction> createNewPaymentTransaction(ReqCreatePaymentWrapperForm reqCreatePaymentWrapperForm, UUID userId) {
        // 1 : create new payment transaction
        Either<ServiceError, PaymentTransaction> paymentTransactionCase = helpCreateNewPaymentTransaction(reqCreatePaymentWrapperForm, userId);
        if (paymentTransactionCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to create new payment transaction, cause by: " + paymentTransactionCase.getLeft().message()));
        PaymentTransaction paymentTransaction = paymentTransactionCase.getRight();

        // 2 : add payment transaction item
        Either<ServiceError, List<PaymentItem>> assignPaymentTransactionItemCase = helpCreatePaymentTransactionItems(
                reqCreatePaymentWrapperForm.getData().getItems(),
                userId
        );
        if (assignPaymentTransactionItemCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to assign payment transaction items, cause by: " + assignPaymentTransactionItemCase.getLeft().message()));
        for (PaymentItem item : assignPaymentTransactionItemCase.getRight()) {
            item.setPayment(paymentTransaction);
            paymentTransaction.getExpenseItems().add(item);
        }

        // 3 : loop through file details and attach them to payment transaction
        Either<ServiceError, List<FileDetail>> paymentTransactionFileUploadCase = helpUploadFileDetails(
                reqCreatePaymentWrapperForm.getFiles(),
                userId
        );
        if (paymentTransactionFileUploadCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to upload file details, cause by: " + paymentTransactionFileUploadCase.getLeft().message()));
        for (FileDetail eachFileDetail : paymentTransactionFileUploadCase.getRight()) {
            paymentTransaction.getFileDetails().add(eachFileDetail);
        }

        return paymentTransactionRepository.createNewPaymentTransaction(paymentTransaction)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Failed to create new payment transaction in repository, cause by: " + error.message())),
                        Either::right
                );
    }

    private Either<ServiceError, PaymentTransaction> helpCreateNewPaymentTransaction(
            ReqCreatePaymentWrapperForm reqCreatePaymentWrapperForm,
            UUID userId
    ) {
        // find user : get user object
        Either<ServiceError, User> findUserCase = userService.findUserById(userId);
        if (findUserCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find user, cause by:" + findUserCase.getLeft().message()));
        User user = findUserCase.getRight();

        // find contact : get contact object
        Either<ServiceError, Contact> findContactCase = contactService.findContactByIdAndUser(reqCreatePaymentWrapperForm.getData().getContact(), userId);
        if (findContactCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find contact, cause by: " + findContactCase.getLeft().message()));
        Contact contact = findContactCase.getRight();

        // find property : get property object
        Either<ServiceError, Property> findPropertyCase = propertyService.findPropertyByIdAndUserId(reqCreatePaymentWrapperForm.getData().getProperty(), userId);
        if (findPropertyCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find property, cause by: " + findPropertyCase.getLeft().message()));
        Property property = findPropertyCase.getRight();

        // find sale transaction : get sale transaction object
        Either<ServiceError, Transaction> paymentTransactionCase = transactionService.getTransactionPrePersist(TransactionChoice.payment, userId, reqCreatePaymentWrapperForm.getData().getNote());
        if (paymentTransactionCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to create payment transaction, cause by: " + paymentTransactionCase.getLeft().message()));
        Transaction transaction = paymentTransactionCase.getRight();

        // create payment transaction object
        return Either.right(
                PaymentTransaction.builder()
                        .property(property)
                        .contact(contact)
                        .transaction(transaction)
                        .expenseItems(new ArrayList<>())
                        .fileDetails(new HashSet<>())
                        .build()
        );
    }

    private Either<ServiceError, List<PaymentItem>> helpCreatePaymentTransactionItems(
            List<ReqCreatePaymentItemDto> paymentItemList,
            UUID userId
    ) {
        List<PaymentItem> itemList = new ArrayList<>();
        // each item in paymentItemList check expense : get expense object
        // if expense not found, return error
        // if expense found, add to payment transaction item list
        for (ReqCreatePaymentItemDto item : paymentItemList) {
            Either<ServiceError, Expense> findExpenseCase = expenseService.findExpenseByIdAndUserId(item.getExpense(), userId);
            if (findExpenseCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to find expense with ID: " + item.getExpense() + ", cause by: " + findExpenseCase.getLeft().message()));
            Expense expense = findExpenseCase.getRight();
            PaymentItem tempPaymentItem = PaymentItem.builder()
                    .expense(expense)
                    .amount(item.getAmount())
                    .price(item.getPrice())
                    .build();
            itemList.add(tempPaymentItem);

        } // end for
        return Either.right(itemList);
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
    public Either<ServiceError, PaymentTransaction> updatePaymentTransaction(ReqUpdatePaymentWrapperForm reqUpdatePaymentWrapperForm, UUID paymentTransactionId, UUID userId) {
        // find payment transaction by ID and user ID
        Either<RepositoryError, PaymentTransaction> paymentTransactionCase = paymentTransactionRepository.findPaymentTransactionByIdAndUserId(paymentTransactionId, userId);
        if (paymentTransactionCase.isLeft()) return Either.left(new ServiceError.NotFound("Payment transaction not found for ID: " + paymentTransactionId + " and user ID: " + userId + ", cause by: " + paymentTransactionCase.getLeft().message()));
        PaymentTransaction paymentTransaction = paymentTransactionCase.getRight();

        // update payment transaction info
        Either<ServiceError, PaymentTransaction> updatePaymentTransactionInfoCase = helpUpdatePaymentTransactionInfo(paymentTransaction, reqUpdatePaymentWrapperForm, userId);
        if (updatePaymentTransactionInfoCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update payment transaction info, cause by: " + updatePaymentTransactionInfoCase.getLeft().message()));
        paymentTransaction = updatePaymentTransactionInfoCase.getRight();
        // update payment transaction items
        Either<ServiceError, PaymentTransaction> updatePaymentTransactionItemsCase = helpUpdatePaymentTransactionItems(paymentTransaction, reqUpdatePaymentWrapperForm.getData().getItems(), userId);
        if (updatePaymentTransactionItemsCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update payment transaction items, cause by: " + updatePaymentTransactionItemsCase.getLeft().message()));
        paymentTransaction = updatePaymentTransactionItemsCase.getRight();
        return paymentTransactionRepository.updatePaymentTransaction(paymentTransaction)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Failed to update payment transaction in repository, cause by: " + error.message())),
                        Either::right
                );
    }

    private Either<ServiceError, PaymentTransaction> helpUpdatePaymentTransactionInfo(
            PaymentTransaction paymentTransaction,
            ReqUpdatePaymentWrapperForm reqUpdatePaymentWrapperForm,
            UUID userId
    ){
        // if the old contact not the same as update contact
        if(!paymentTransaction.getContact().getId().equals(reqUpdatePaymentWrapperForm.getData().getContact())) {
            Either<ServiceError, Contact> updatedContactCase = contactService.findContactByIdAndUser(reqUpdatePaymentWrapperForm.getData().getContact(), userId);
            if (updatedContactCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update contact, cause by: " + updatedContactCase.getLeft().message()));
            Contact updatedContact = updatedContactCase.getRight();
            paymentTransaction.setContact(updatedContact);
        }
        // if property not the same as update property
        if (!paymentTransaction.getProperty().getId().equals(reqUpdatePaymentWrapperForm.getData().getProperty())) {
            Either<ServiceError, Property> updatedPropertyCase = propertyService.findPropertyByIdAndUserId(reqUpdatePaymentWrapperForm.getData().getProperty(), userId);
            if (updatedPropertyCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update property, cause by: " + updatedPropertyCase.getLeft().message()));
            Property updatedProperty = updatedPropertyCase.getRight();
            paymentTransaction.setProperty(updatedProperty);
        }
        // if note not the same as update note
        if (!paymentTransaction.getTransaction().getNote().equals(reqUpdatePaymentWrapperForm.getData().getNote())) {
            paymentTransaction.getTransaction().setNote(reqUpdatePaymentWrapperForm.getData().getNote());
        }

        return Either.right(paymentTransaction);
    }


    private Either<ServiceError, PaymentTransaction> helpUpdatePaymentTransactionItems(
            PaymentTransaction paymentTransaction,
            List<ReqUpdatePaymentItemDto> reqUpdatePaymentItemDtoList,
            UUID userId
    ) {
        // if id is null, check expense and add new item to payment transaction
        // if it has id, find item with id check expense and update it
        for (ReqUpdatePaymentItemDto reqUpdatePaymentItemDto : reqUpdatePaymentItemDtoList) {
            if (reqUpdatePaymentItemDto.getId() == null) {
                // case 1: id is null, create new payment item
                Either<ServiceError, PaymentItem> newPaymentItemCase = helpUpdatePaymentItemCaseNullId(reqUpdatePaymentItemDto, userId);
                if (newPaymentItemCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to create new payment item, cause by: " + newPaymentItemCase.getLeft().message()));
                PaymentItem newPaymentItem = newPaymentItemCase.getRight();
                paymentTransaction.getExpenseItems().add(newPaymentItem);
            } else {
                // case 2: id exist, update payment item
                Either<ServiceError, PaymentItem> updatedPaymentItemCase = helpUpdatePaymentItemCaseIdExist(reqUpdatePaymentItemDto, paymentTransaction, userId);
                if (updatedPaymentItemCase.isLeft()) return Either.left(new ServiceError.OperationFailed("Failed to update payment item with ID: " + reqUpdatePaymentItemDto.getId() + ", cause by: " + updatedPaymentItemCase.getLeft().message()));
            }
        }
        return Either.right(paymentTransaction);
    }

    private Either<ServiceError, PaymentItem> helpUpdatePaymentItemCaseNullId(
            ReqUpdatePaymentItemDto reqUpdatePaymentItemDto,
            UUID userId
    ){
        Either<ServiceError, Expense> expenseExistCase = expenseService.findExpenseByIdAndUserId(reqUpdatePaymentItemDto.getExpense(), userId);
        if (expenseExistCase.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to find expense with ID: " + reqUpdatePaymentItemDto.getExpense() + ", cause by: " + expenseExistCase.getLeft().message()));
        }
        Expense expense = expenseExistCase.getRight();
        PaymentItem paymentItem = PaymentItem.builder()
                .expense(expense)
                .amount(reqUpdatePaymentItemDto.getAmount())
                .price(reqUpdatePaymentItemDto.getPrice())
                .build();
        return Either.right(paymentItem);
    }

    private Either<ServiceError, PaymentItem> helpUpdatePaymentItemCaseIdExist(
            ReqUpdatePaymentItemDto reqUpdatePaymentItemDto,
            PaymentTransaction paymentTransaction,
            UUID userId
    ){
        // find payment item by id in payment transaction
        // check expense id
        // update payment item with new expense, amount and price
        for (PaymentItem paymentItem : paymentTransaction.getExpenseItems()) {
            if (paymentItem.getId().equals(reqUpdatePaymentItemDto.getId())) {
                // check expense
                Either<ServiceError, Expense> expenseExistCase = expenseService.findExpenseByIdAndUserId(reqUpdatePaymentItemDto.getExpense(), userId);
                if (expenseExistCase.isLeft()) {
                    return Either.left(new ServiceError.OperationFailed("Failed to find expense with ID: " + reqUpdatePaymentItemDto.getExpense() + ", cause by: " + expenseExistCase.getLeft().message()));
                }
                Expense expense = expenseExistCase.getRight();
                paymentItem.setExpense(expense);
                paymentItem.setAmount(reqUpdatePaymentItemDto.getAmount());
                paymentItem.setPrice(reqUpdatePaymentItemDto.getPrice());
                return Either.right(paymentItem);
            }
        }
        return Either.left(new ServiceError.NotFound("Payment item with ID: " + reqUpdatePaymentItemDto.getId() + " not found in payment transaction"));
    }




}
