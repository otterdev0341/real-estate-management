package sale.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.SaleTransaction;
import common.errorStructure.ServiceError;
import sale.domain.dto.ReqCreateSaleDto;
import sale.domain.dto.ReqUpdateSaleDto;

import java.util.List;
import java.util.UUID;

public interface InternalSaleTransactionService {

    Either<ServiceError, SaleTransaction> createNewSaleTransaction(ReqCreateSaleDto reqCreateSaleDto, UUID userId);

    Either<ServiceError, SaleTransaction> updateSaleTransaction(ReqUpdateSaleDto reqUpdateSaleDto, UUID saleTransactionId, UUID userId);

    Either<ServiceError, SaleTransaction> findSaleTransactionByIdWithUserId(UUID transactionId, UUID userId);

    Either<ServiceError, Boolean> deleteSaleTransaction(UUID saleTransactionId, UUID userId);

    Either<ServiceError, List<SaleTransaction>> findAllSaleTransactionWithUserId(UUID userId, BaseQuery query);

}
