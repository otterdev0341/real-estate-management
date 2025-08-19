package transaction.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.entity.Contact;
import common.domain.entity.Transaction;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import transaction.repository.internal.InternalTransactionRepository;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class TransactionRepositoryImpl implements PanacheRepositoryBase<Transaction, UUID>, InternalTransactionRepository {

    @Override
    public Either<RepositoryError, Boolean> isTransactionExist(UUID transactionId, UUID userId) {
        try {
            Boolean isTransactionExist = find("id = ?1 and createdBy.id = ?2", transactionId, userId).firstResultOptional().isPresent();
            return Either.right(isTransactionExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch transaction" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Transaction> updateTransaction(Transaction transaction) {
        try {
            Transaction mergedTransaction = getEntityManager().merge(transaction);
            getEntityManager().flush();
            return Either.right(mergedTransaction);
        } catch (Exception e)
        {
            return Either.left(new RepositoryError.UpdateFailed("Failed to update transaction" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Transaction> getTransactionByIdWithUserId(UUID transactionId, UUID userId) {
        try {
            Optional<Transaction> transactionOptional = find("id = ?1 and createdBy.id = ?2", transactionId, userId).firstResultOptional();
            return transactionOptional
                    .<Either<RepositoryError, Transaction>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Transaction not found")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch transaction" + e.getMessage()));
        }
    }


}
