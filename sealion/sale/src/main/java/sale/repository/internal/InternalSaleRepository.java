package sale.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.SaleTransaction;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalSaleRepository {

    Either<RepositoryError, SaleTransaction> createNewSaleTransaction(SaleTransaction saleTransaction);

    Either<RepositoryError, SaleTransaction> updateSaleTransaction(SaleTransaction saleTransaction);

    Either<RepositoryError, SaleTransaction> findSaleTransactionByIdAndUserId(UUID saleTransactionId, UUID userId);

    Either<RepositoryError, Boolean> deleteSaleTransactionByIdAndUserId(UUID saleTransactionId, UUID userId);

    Either<RepositoryError, Boolean> isSaleTransactionExist(UUID saleTransactionId, UUID userId);

    Either<RepositoryError, List<SaleTransaction>> findAllSaleTransactionsWithUserId(UUID userId, BaseQuery query);

}
