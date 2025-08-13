package expense.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ExpenseType;
import common.domain.entity.User;
import common.errorStructure.ServiceError;
import expense.domain.dto.expenseType.ReqCreateExpenseTypeDto;
import expense.domain.dto.expenseType.ReqUpdateExpenseTypeDto;
import expense.domain.dto.expenseType.ResEntryExpenseTypeDto;
import expense.domain.mapper.ExpenseTypeMapper;
import expense.repository.internal.InternalExpenseTypeRepository;
import expense.service.declare.DeclareExpenseTypeService;
import expense.service.internal.InternalExpenseTypeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

@ApplicationScoped
public class ExpenseTypeService implements DeclareExpenseTypeService, InternalExpenseTypeService {

    private final DeclareUserService userService;
    private final InternalExpenseTypeRepository expenseTypeRepository;
    private final ExpenseTypeMapper expenseTypeMapper;

    @Inject
    public ExpenseTypeService(
            DeclareUserService userService,
            InternalExpenseTypeRepository expenseTypeRepository,
            ExpenseTypeMapper expenseTypeMapper
    ) {
        this.userService = userService;
        this.expenseTypeRepository = expenseTypeRepository;
        this.expenseTypeMapper = expenseTypeMapper;
    }

    @Override
    public Either<ServiceError, Boolean> isExpenseTypeExistWithUserId(UUID expenseTypeId, UUID userId) {
        return expenseTypeRepository.isExistByIdAndUserId(expenseTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to check is expense type exist" + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, ResEntryExpenseTypeDto> findTheExpenseTypeWithUserId(UUID userId, UUID expenseTypeId) {
        return expenseTypeRepository.findExpenseTypeAndUserId(expenseTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to find expense type reason by" + error.message());
                            return Either.left(theError);
                        },
                        success -> {
                            ResEntryExpenseTypeDto dto = expenseTypeMapper.toDto(success);
                            return Either.right(dto);
                        }
                );
    }

    @Override
    public Either<ServiceError, ResEntryExpenseTypeDto> createNewExpenseType(UUID userId, ReqCreateExpenseTypeDto expenseTypeDto) {
        // check is user exist
        // check is expense type detail is not exist
        // create new expense type
        return userService.findUserById(userId)
                .flatMapLeft(Either::left)
                .flatMapRight(user -> {
                    return expenseTypeRepository.isExistByDetailAndUserId(expenseTypeDto.getDetail().trim(), userId)
                            .mapRight(isExist -> Pair.of(user, isExist))
                            .mapLeft(repoErr -> new ServiceError.ValidationFailed("failed to check expense type detail:"+repoErr.message()));

                })
                .flatMapRight(pair -> {
                    User user = pair.getLeft();
                    Boolean isExist = pair.getRight();
                    if (isExist) {
                        return Either.left(new ServiceError.DuplicateEntry("Expense type already exist for this user"));
                    }
                    ExpenseType expenseType = ExpenseType.builder()
                            .detail(expenseTypeDto.getDetail().trim())
                            .createdBy(user)
                            .build();
                    return expenseTypeRepository.createExpenseType(expenseType)
                            .flatMapRight(success -> {
                                ResEntryExpenseTypeDto dto = expenseTypeMapper.toDto(success);
                                return Either.right(dto);

                            })
                            .flatMapLeft(error -> {
                                return Either.left(new ServiceError.PersistenceFailed("Failed to create new expense type: " + error.message()));
                            });
                });
    }



    @Override
    public Either<ServiceError, ResEntryExpenseTypeDto> updateExpenseType(UUID userId, UUID expenseTypeId, ReqUpdateExpenseTypeDto expenseTypeDto) {
        // check is expense exist
        // check is new expense didn't exist
        // persist update
        return expenseTypeRepository.findExpenseTypeAndUserId(expenseTypeId, userId)
                .flatMapLeft(error -> {
                    ServiceError theError = new ServiceError.OperationFailed("Failed to fetch expense type reason by" + error.message());
                    return Either.left(theError);
                })
                .flatMapRight(expenseType -> {
                    return expenseTypeRepository.isExistByDetailAndUserId(expenseTypeDto.getDetail().trim(), userId)
                            .mapRight(exist -> Pair.of(expenseType, exist))
                            .mapLeft(error -> new ServiceError.OperationFailed("Failed to check is new expense type exist" + error.message()));

                }).flatMapRight(pair -> {
                    ExpenseType expenseType = pair.getLeft();
                    Boolean isExist = pair.getRight();
                    if (isExist) {
                        return Either.left(new ServiceError.DuplicateEntry("The expense detail already exist"));
                    }
                    expenseType.setDetail(expenseTypeDto.getDetail().trim());
                    return expenseTypeRepository.updateExpenseType(expenseType)
                            .flatMapLeft(thisError -> {
                                ServiceError finalError = new ServiceError.OperationFailed("Failed to check is new expense type exist" + thisError.message());
                                return Either.left(finalError);

                            })
                            .flatMapRight(success -> {
                                ResEntryExpenseTypeDto dto = expenseTypeMapper.toDto(success);
                                return Either.right(dto);
                            });
                });
    }



    @Override
    public Either<ServiceError, Boolean> deleteExpenseType(UUID userId, UUID expenseTypeId) {
        return expenseTypeRepository.deleteExpenseTypeByIdAndUserId(expenseTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to delete reason by " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, ExpenseType> findExpenseTypeByIdAndUserId(UUID expenseTypeId, UUID userId) {
        return expenseTypeRepository.findExpenseTypeAndUserId(expenseTypeId, userId)
                .fold(
                        error -> {
                    ServiceError theError = new ServiceError.OperationFailed("Fail to fetch expense type or not found reason by" + error.message());
                    return Either.left(theError);
                },
                Either::right);
    }

    @Override
    public Either<ServiceError, ResListBaseDto<ResEntryExpenseTypeDto>> findAllExpenseTypesByUserId(UUID userId, BaseQuery query) {
        return expenseTypeRepository.findAllExpenseTypeWithUserId(userId, query)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Fail to fetch expense type or not found reason by" + error.message());
                            return Either.left(theError);
                        },
                        success -> {
                            ResListBaseDto<ResEntryExpenseTypeDto> payload = expenseTypeMapper.toResListBaseDto("Expense type list fetch successfully",success);
                            return Either.right(payload);
                        }
                );
    }
}
