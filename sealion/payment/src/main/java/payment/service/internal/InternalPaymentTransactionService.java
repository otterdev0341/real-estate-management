package payment.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.ServiceError;
import payment.domain.dto.wrapper.ReqCreatePaymentWrapperForm;
import payment.domain.dto.wrapper.ReqUpdatePaymentWrapperForm;

import java.util.List;
import java.util.UUID;

public interface InternalPaymentTransactionService {

    Either<ServiceError, PaymentTransaction> createNewPaymentTransaction(ReqCreatePaymentWrapperForm reqCreatePaymentWrapperForm, UUID userId);
    Either<ServiceError, PaymentTransaction> updatePaymentTransaction(ReqUpdatePaymentWrapperForm reqUpdatePaymentWrapperForm, UUID paymentTransactionId , UUID userId);

    Either<ServiceError, PaymentTransaction> findPaymentTransactionByIdAndUserId(UUID paymentTransactionId, UUID userId);
    Either<ServiceError, Boolean> deletePaymentTransactionById(UUID paymentTransactionId, UUID userId);
    Either<ServiceError, List<PaymentTransaction>> findAllPaymentTransactionWithUserId(UUID userId, BaseQuery query);

}
