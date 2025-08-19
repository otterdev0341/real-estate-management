package transaction.service.declare;

import com.spencerwi.either.Either;
import common.domain.entity.Transaction;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import transaction.entity.choice.TransactionChoice;

import java.util.UUID;

public interface DeclareTransactionService {

    Either<ServiceError, Transaction> getTransactionPrePersist(TransactionChoice choice, UUID userId, String note);


    Either<ServiceError, Boolean> isTransactionExist(UUID transactionId, UUID userId);

    Either<ServiceError, Transaction> updateTransaction(Transaction transaction, UUID userId);

    Either<ServiceError, Transaction> getTransactionByIdWithUserId(UUID transactionId, UUID userId);



}
