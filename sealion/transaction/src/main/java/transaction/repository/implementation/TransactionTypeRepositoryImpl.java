package transaction.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.entity.TransactionType;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import transaction.repository.internal.InternalTransactionTypeRepository;

import java.util.Optional;
import java.util.UUID;

enum transactionType {
    sale,
    investment,
    payment
}



@ApplicationScoped
public class TransactionTypeRepositoryImpl implements PanacheRepositoryBase<TransactionType, UUID>, InternalTransactionTypeRepository {

    @Override
    public Either<RepositoryError, TransactionType> getSaleTransactionType() {
        try {
            Optional<TransactionType> transactionTypeOptional = find("detail = ?1", transactionType.sale.toString()).firstResultOptional();
            return transactionTypeOptional
                    .<Either<RepositoryError, TransactionType>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Sale transaction type not found")));

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch sale transaction type" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, TransactionType> getInvestmentTransactionType() {
        try {
            Optional<TransactionType> transactionTypeOptional = find("detail = ?1", transactionType.investment).firstResultOptional();
            return transactionTypeOptional
                    .<Either<RepositoryError, TransactionType>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Investment transaction type not found")));

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch sale transaction type" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, TransactionType> getPaymentTransactionType() {
        try {
            Optional<TransactionType> transactionTypeOptional = find("detail = ?1", transactionType.payment).firstResultOptional();
            return transactionTypeOptional
                    .<Either<RepositoryError, TransactionType>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Investment transaction type not found")));

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch sale transaction type" + e.getMessage()));
        }
    }
}
