package transaction.service.implementation;


import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.entity.Transaction;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import transaction.entity.choice.TransactionChoice;
import transaction.repository.internal.InternalTransactionRepository;
import transaction.repository.internal.InternalTransactionTypeRepository;
import transaction.service.declare.DeclareTransactionService;

import java.util.UUID;
import java.util.function.Function;

@ApplicationScoped
public class TransactionService implements DeclareTransactionService {

    private final InternalTransactionTypeRepository transactionTypeRepository;
    private final InternalTransactionRepository transactionRepository;
    private final DeclareUserService userService;

    @Inject
    public TransactionService(
            InternalTransactionTypeRepository transactionTypeRepository,
            InternalTransactionRepository transactionRepository,
            DeclareUserService userService
    ) {
        this.transactionTypeRepository = transactionTypeRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    @Override
    public Either<ServiceError, Transaction> getTransactionPrePersist(TransactionChoice choice, UUID userId, String note) {
        // user,
        // transaction,
        // assign note, user object, transaction type
        return userService.findUserById(userId)
                .mapLeft(userError -> userError )
                .flatMapRight(foundedUser -> {
                    if (choice == TransactionChoice.sale) {
                        return transactionTypeRepository.getSaleTransactionType()
                                .mapRight(foundedTransactionType -> Pair.of(foundedUser, foundedTransactionType))
                                .mapLeft(transactionTypeError -> new ServiceError.OperationFailed("Failed to retrieved sale transaction type"));
                    } else if ( choice == TransactionChoice.payment) {
                        return transactionTypeRepository.getPaymentTransactionType()
                                .mapRight(foundedTransactionType -> Pair.of(foundedUser, foundedTransactionType))
                                .mapLeft(transactionTypeError -> new ServiceError.OperationFailed("Failed to retrieved payment transaction type"));
                    } else {
                        return transactionTypeRepository.getInvestmentTransactionType()
                                .mapRight(foundedTransactionType -> Pair.of(foundedUser, foundedTransactionType))
                                .mapLeft(transactionTypeError -> new ServiceError.OperationFailed("Failed to retrieved investment transaction type"));
                    }




                })
                .flatMapRight(pair -> {
                    Transaction transaction = new Transaction();
                    transaction.setNote(note);
                    transaction.setCreatedBy(pair.getLeft());
                    transaction.setTransactionType(pair.getRight());
                    return Either.right(transaction);
                });
    }


    @Override
    public Either<ServiceError, Boolean> isTransactionExist(UUID transactionId, UUID userId) {

        return transactionRepository.isTransactionExist(transactionId, userId)
                .fold(
                        error -> Either.left(new ServiceError.OperationFailed("Failed to check is transaction exist" + error.message())),
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Transaction> updateTransaction(Transaction transaction, UUID userId) {
        return transactionRepository.updateTransaction(transaction)
                .fold(
                        error -> {
                          return Either.left(new ServiceError.OperationFailed("Failed to update transaction cause by:" + error.message()));
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Transaction> getTransactionByIdWithUserId(UUID transactionId, UUID userId) {
        return transactionRepository.getTransactionByIdWithUserId(transactionId, userId)
                .fold(
                        error -> {

                            return Either.left(new ServiceError.OperationFailed("Failed to find transaction, cause by : " + error.message()));
                        },
                        Either::right
                );
    }
}
