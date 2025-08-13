package expense.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Expense;
import common.domain.entity.ExpenseType;
import common.domain.entity.User;
import common.errorStructure.ServiceError;
import expense.domain.dto.expense.ReqCreateExpenseDto;
import expense.domain.dto.expense.ReqUpdateExpenseDto;
import expense.domain.dto.expense.ResEntryExpenseDto;
import expense.domain.dto.expenseType.ResEntryExpenseTypeDto;
import expense.domain.mapper.ExpenseMapper;
import expense.repository.internal.InternalExpenseRepository;
import expense.repository.internal.InternalExpenseTypeRepository;
import expense.service.declare.DeclareExpenseService;
import expense.service.internal.InternalExpenseService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;


@ApplicationScoped
public class ExpenseService  implements DeclareExpenseService, InternalExpenseService {

    private final DeclareUserService userService;
    private final InternalExpenseTypeRepository expenseTypeRepository;
    private final InternalExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Inject
    public ExpenseService(DeclareUserService userService, InternalExpenseTypeRepository expenseTypeRepository, InternalExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.userService = userService;
        this.expenseTypeRepository = expenseTypeRepository;
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public Either<ServiceError, Boolean> isExpenseExistWithUserId(UUID expenseId, UUID userId) {
        return expenseRepository.isExistByIdAndUserId(expenseId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to check is expense exist reason by" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Expense> findExpenseByIdAndUserId(UUID expenseId, UUID userId) {
        return expenseRepository.findExpenseAndUserId(expenseId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to fetch expense reason by" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, ResEntryExpenseDto> createNewExpense(UUID userId, ReqCreateExpenseDto expenseDto) {
        // check is user exist -> carring user
        // check is expense type exist -> carring user, expense type
        // check is new expense detail exist
        // persist
        return userService.findUserById(userId)
                .flatMapLeft(Either::left)
                .flatMapRight(user -> {
                    return expenseRepository.isExistByDetailAndUserId(expenseDto.getDetail().trim(), userId)
                            .mapRight(isExist -> Pair.of(user, isExist))
                            .mapLeft(repoErr -> new ServiceError.ValidationFailed("failed to check expense type detail:"+repoErr.message()));

                })
                .flatMapRight(pair -> {
                    User user = pair.getLeft();
                    Boolean isExpenseDetailExist = pair.getRight();
                    if (isExpenseDetailExist) {
                        return Either.left(new ServiceError.DuplicateEntry("Expense already exist for this user"));
                    }

                    return expenseTypeRepository.findExpenseTypeAndUserId(expenseDto.getExpenseType(), userId)
                            .mapRight(expenseTypeExist -> Pair.of(user, expenseTypeExist))
                            .mapLeft(repoErr -> new ServiceError.ValidationFailed("failed to check expense type by" + repoErr.message()));

                }).flatMapRight(finalPair -> {
                    User user = finalPair.getLeft();
                    ExpenseType expenseType = finalPair.getRight();
                    Expense expense = Expense.builder()
                            .detail(expenseDto.getDetail().trim())
                            .expenseType(expenseType)
                            .createdBy(user)
                            .build();
                    return expenseRepository.createExpense(expense)
                            .flatMapLeft(repoErr -> {
                                ServiceError theError = new ServiceError.OperationFailed("Failed to create expense reason by: " + repoErr.message());
                                return Either.left(theError);
                            })
                            .flatMapRight(success -> {
                               ResEntryExpenseDto payload = expenseMapper.toDto(success);
                                 return Either.right(payload);
                            });
                });
    }// end class

    @Override
    public Either<ServiceError, ResEntryExpenseDto> updateExpense(UUID userId, UUID expenseId, ReqUpdateExpenseDto expenseDto) {
        // check is expense exist
        // check is new expense didn't exist
        // check is expense type exist
        // prepare payload
        // persist update
        return expenseRepository.findExpenseAndUserId(expenseId, userId)
                .flatMapLeft(error -> {
                    ServiceError theError = new ServiceError.OperationFailed("Failed to fetch expense or expense not found" + error.message());
                    return Either.left(theError);
                })
                .flatMapRight(expenseExist -> {
                    return expenseRepository.isExistByDetailAndUserId(expenseDto.getDetail(), userId)
                            .mapRight(isExist -> Pair.of(expenseExist, isExist))
                            .mapLeft(error -> new ServiceError.OperationFailed("Failed to check is new expense exist" + error.message()));
                })
                .flatMapRight(pair -> {
                    Expense expense = pair.getLeft();
                    Boolean isNewDetailDetailExist = pair.getRight();
                    if (isNewDetailDetailExist) {
                        return Either.left(new ServiceError.DuplicateEntry("Expense already exist for this user"));
                    }
                    return expenseTypeRepository.findExpenseTypeAndUserId(expenseDto.getExpenseType(), userId)
                            .mapRight(expenseTypeExist -> Pair.of(expense, expenseTypeExist))
                            .mapLeft(repoErr -> new ServiceError.ValidationFailed("failed to check expense type by" + repoErr.message()));
                })
                .flatMapRight(pair -> {
                    Expense expense = pair.getLeft();
                    ExpenseType expenseType = pair.getRight();
                    expense.setDetail(expenseDto.getDetail().trim());
                    expense.setExpenseType(expenseType);
                    return expenseRepository.updateExpense(expense)
                            .flatMapLeft(repoErr -> {
                                ServiceError theError = new ServiceError.OperationFailed("Failed to update expense reason by: " + repoErr.message());
                                return Either.left(theError);
                            })
                            .flatMapRight(success -> {
                                ResEntryExpenseDto payload = expenseMapper.toDto(success);
                                return Either.right(payload);
                            });
                });
    } // end class

    @Override
    public Either<ServiceError, Boolean> deleteExpense(UUID userId, UUID expenseId) {
        return expenseRepository.deleteExpenseByIdAndUserId(expenseId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to delete expense reason by" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, ResEntryExpenseDto> findTheExpenseByIdAndUserId(UUID userId, UUID expenseId) {
        return expenseRepository.findExpenseAndUserId(expenseId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("failed to find expense reason by: " + error.message());
                            return Either.left(theError);
                        },
                        success -> {
                            ResEntryExpenseDto payload = expenseMapper.toDto(success);
                            return Either.right(payload);
                        }
                );
    }

    @Override
    public Either<ServiceError, ResListBaseDto<ResEntryExpenseDto>> findAllExpensesByUserId(UUID userId, BaseQuery query) {
        return expenseRepository.findAllExpenseWithUserId(userId, query)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("failed to find expense reason by: " + error.message());
                      return Either.left(theError);
                  },
                  success -> {
                      ResListBaseDto<ResEntryExpenseDto> payload = expenseMapper.toResListBaseDto("Expense fetch successfully", success);
                      return Either.right(payload);
                  }
                );
    }
}
