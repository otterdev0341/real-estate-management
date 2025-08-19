package payment.service.implementation;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.ServiceError;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import payment.domain.dto.wrapper.ReqCreatePaymentWrapperForm;
import payment.domain.dto.wrapper.ReqUpdatePaymentWrapperForm;
import payment.service.internal.InternalPaymentTransactionService;

import java.util.List;
import java.util.UUID;


@ApplicationScoped
@Named("paymentTransactionService")
public class PaymentTransactionServiceImpl implements InternalPaymentTransactionService, FileAssetManagementService {
    

    @Override
    public Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId, FileUpload targetFile) {
        return null;
    }

    @Override
    public Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId, UUID userId, UUID fileId) {
        return null;
    }

    @Override
    public Either<ServiceError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        return null;
    }

    @Override
    public Either<ServiceError, PaymentTransaction> createNewPaymentTransaction(ReqCreatePaymentWrapperForm reqCreatePaymentWrapperForm, UUID userId) {
        return null;
    }

    @Override
    public Either<ServiceError, PaymentTransaction> updatePaymentTransaction(ReqUpdatePaymentWrapperForm reqUpdatePaymentWrapperForm, UUID paymentTransactionId, UUID userId) {
        return null;
    }

    @Override
    public Either<ServiceError, PaymentTransaction> findPaymentTransactionByIdAndUserId(UUID paymentTransactionId, UUID userId) {
        return null;
    }

    @Override
    public Either<ServiceError, Boolean> deletePaymentTransactionById(UUID paymentTransactionId, UUID userId) {
        return null;
    }

    @Override
    public Either<ServiceError, List<PaymentTransaction>> findAlPaymentTransactionWithUserId(UUID userId, BaseQuery query) {
        return null;
    }
}
