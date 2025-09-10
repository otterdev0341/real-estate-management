package payment.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.UUID;

public interface InternalPaymentTransactionRepository  {

    Either<RepositoryError, PaymentTransaction> createNewPaymentTransaction(PaymentTransaction paymentTransaction);
    Either<RepositoryError, PaymentTransaction> updatePaymentTransaction(PaymentTransaction paymentTransaction);
    Either<RepositoryError, PaymentTransaction> findPaymentTransactionByIdAndUserId(UUID paymentTransactionId, UUID userId);
    Either<RepositoryError, Boolean> deletePaymentTransactionById(UUID paymentTransactionId, UUID userId);
    Either<RepositoryError, List<PaymentTransaction>> findAllPaymentTransactionWithUserId(UUID userId, BaseQuery query);

}
