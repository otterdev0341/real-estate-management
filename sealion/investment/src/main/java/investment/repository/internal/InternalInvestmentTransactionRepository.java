package investment.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.investment.InvestmentTransaction;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalInvestmentTransactionRepository {

    Either<RepositoryError, InvestmentTransaction> createNewInvestmentTransaction(InvestmentTransaction investmentTransaction);
    Either<RepositoryError, InvestmentTransaction> updateInvestmentTransaction(InvestmentTransaction investmentTransaction);
    Either<RepositoryError, InvestmentTransaction> findInvestmentTransactionByIdAndUserId(UUID investmentTransactionId, UUID userId);
    Either<RepositoryError, Boolean> deleteInvestmentTransactionById(UUID investmentTransactionId, UUID userId);
    Either<RepositoryError, List<InvestmentTransaction>> findAllInvestmentTransactionWIthUserId(UUID userId, BaseQuery query);


}
