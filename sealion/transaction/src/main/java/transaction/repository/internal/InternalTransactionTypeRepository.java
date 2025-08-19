package transaction.repository.internal;

import com.spencerwi.either.Either;
import common.domain.entity.TransactionType;
import common.errorStructure.RepositoryError;

public interface InternalTransactionTypeRepository {

    Either<RepositoryError, TransactionType> getSaleTransactionType();

    Either<RepositoryError, TransactionType> getInvestmentTransactionType();

    Either<RepositoryError, TransactionType> getPaymentTransactionType();

}
