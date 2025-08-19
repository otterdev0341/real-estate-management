package payment.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.SaleTransaction;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.RepositoryError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import payment.repository.internal.InternalPaymentTransactionRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@Named("paymentTransactionRepository")
public class PaymentTransactionRepositoryImpl implements PanacheRepositoryBase<SaleTransaction, UUID>, InternalPaymentTransactionRepository, FileAssetManagementRepository {


    @Override
    public Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        return null;
    }

    @Override
    public Either<RepositoryError, PaymentTransaction> createNewPaymentTransaction(PaymentTransaction paymentTransaction) {
        return null;
    }

    @Override
    public Either<RepositoryError, PaymentTransaction> updatePaymentTransaction(PaymentTransaction paymentTransaction) {
        return null;
    }

    @Override
    public Either<RepositoryError, PaymentTransaction> findPaymentTransactionByIdAndUserId(UUID paymentTransactionId, UUID userId) {
        return null;
    }

    @Override
    public Either<RepositoryError, Boolean> deletePaymentTransactionById(UUID paymentTransactionId, UUID userId) {
        return null;
    }

    @Override
    public Either<RepositoryError, List<PaymentTransaction>> findAlPaymentTransactionWithUserId(UUID userId, BaseQuery query) {
        return null;
    }
}
