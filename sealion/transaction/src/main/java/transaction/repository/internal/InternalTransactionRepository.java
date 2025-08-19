package transaction.repository.internal;

import com.spencerwi.either.Either;
import common.domain.entity.Transaction;
import common.errorStructure.RepositoryError;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

import java.util.UUID;

public interface InternalTransactionRepository {

    Either<RepositoryError, Boolean> isTransactionExist(UUID transactionId, UUID userId);

    Either<RepositoryError, Transaction> updateTransaction(Transaction transaction);

    Either<RepositoryError, Transaction> getTransactionByIdWithUserId(UUID transactionId, UUID userId);


}
