package investment.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.investment.InvestmentTransaction;
import common.errorStructure.ServiceError;
import investment.domain.dto.wrapper.ReqCreateInvestmentWrapperForm;
import investment.domain.dto.wrapper.ReqUpdateInvestmentWrapper;

import java.util.List;
import java.util.UUID;

public interface InternalInvestmentTransactionService {

    Either<ServiceError, InvestmentTransaction> createNewInvestmentTransaction(ReqCreateInvestmentWrapperForm reqCreateInvestmentWrapperForm, UUID userId);
    Either<ServiceError, InvestmentTransaction> updateInvestmentTransaction(ReqUpdateInvestmentWrapper reqUpdateInvestmentWrapper, UUID investmentTransactionId , UUID userId);
    Either<ServiceError, InvestmentTransaction> findInvestmentTransactionByIdAndUserId(UUID investmentTransactionId, UUID userId);
    Either<ServiceError, Boolean> deleteInvestmentTransactionById(UUID investmentTransactionId, UUID userId);
    Either<ServiceError, List<InvestmentTransaction>> findAllInvestmentTransactionWithUserId(UUID userId, BaseQuery query);

}
