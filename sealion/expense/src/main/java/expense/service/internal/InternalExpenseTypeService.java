package expense.service.internal;

import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ExpenseType;
import common.errorStructure.ServiceError;
import expense.domain.dto.expenseType.ReqCreateExpenseTypeDto;
import expense.domain.dto.expenseType.ReqUpdateExpenseTypeDto;
import expense.domain.dto.expenseType.ResEntryExpenseTypeDto;

import java.util.UUID;

public interface InternalExpenseTypeService {

    Either<ServiceError, ResEntryExpenseTypeDto> createNewExpenseType(UUID userId, ReqCreateExpenseTypeDto expenseTypeDto);

    Either<ServiceError, ResEntryExpenseTypeDto> updateExpenseType(UUID userId, UUID expenseTypeId, ReqUpdateExpenseTypeDto expenseTypeDto);

    Either<ServiceError, ResEntryExpenseTypeDto> findTheExpenseTypeWithUserId(UUID userId, UUID expenseTypeId);

    Either<ServiceError, Boolean> deleteExpenseType(UUID userId, UUID expenseTypeId);

    Either<ServiceError, ExpenseType> findExpenseTypeByIdAndUserId(UUID userId, UUID expenseTypeId);

    Either<ServiceError, ResListBaseDto<ResEntryExpenseTypeDto>> findAllExpenseTypesByUserId(UUID userId, BaseQuery query);

}
