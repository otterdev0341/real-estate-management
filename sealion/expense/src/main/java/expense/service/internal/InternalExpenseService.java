package expense.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.errorStructure.ServiceError;
import expense.domain.dto.expense.ReqCreateExpenseDto;
import expense.domain.dto.expense.ReqUpdateExpenseDto;
import expense.domain.dto.expense.ResEntryExpenseDto;

import java.util.UUID;

public interface InternalExpenseService {

    Either<ServiceError, ResEntryExpenseDto> createNewExpense(UUID userId, ReqCreateExpenseDto expenseDto);

    Either<ServiceError, ResEntryExpenseDto> updateExpense(UUID userId, UUID expenseId, ReqUpdateExpenseDto expenseDto);

    Either<ServiceError, Boolean> deleteExpense(UUID userId, UUID expenseId);

    Either<ServiceError, ResEntryExpenseDto> findTheExpenseByIdAndUserId(UUID userId, UUID expenseId);

    Either<ServiceError, ResListBaseDto<ResEntryExpenseDto>> findAllExpensesByUserId(UUID userId, BaseQuery query);

}
