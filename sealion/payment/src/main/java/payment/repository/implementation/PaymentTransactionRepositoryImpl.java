package payment.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.RepositoryError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import payment.domain.comparator.PaymentTransactionComparator;
import payment.repository.internal.InternalPaymentTransactionRepository;


import java.util.*;
import java.util.stream.Stream;

@ApplicationScoped
@Named("paymentTransactionRepository")
public class PaymentTransactionRepositoryImpl implements PanacheRepositoryBase<PaymentTransaction, UUID>, InternalPaymentTransactionRepository, FileAssetManagementRepository {

    @Inject
    EntityManager entityManager;

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        try {
            // Find the specific memo first
            Either<RepositoryError, PaymentTransaction> paymentTransactionExist = findPaymentTransactionByIdAndUserId(targetId, userId);
            if (paymentTransactionExist.isLeft()) {
                return Either.left(new RepositoryError.NotFound("Payment transaction not found"));
            }
            PaymentTransaction property = paymentTransactionExist.getRight();

            // Get the files from the properties fileDetails collection
            Set<FileDetail> fileDetails = property.getFileDetails();

            // Dynamically filter the files based on the criteria
            Stream<FileDetail> fileStream = fileDetails.stream();

            if (fileCase.equals(FileCaseSelect.ALL)) {
                // No additional filtering needed
                return Either.right(fileStream.toList());
            }

            List<FileDetail> result = fileStream
                    .filter(fileDetail -> FileCaseSelect.fileCaseMatches(fileDetail, fileCase))
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch file related of property: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PaymentTransaction> createNewPaymentTransaction(PaymentTransaction paymentTransaction) {
        try {
            entityManager.persist(paymentTransaction);
            return Either.right(paymentTransaction);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create Payment Transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PaymentTransaction> updatePaymentTransaction(PaymentTransaction paymentTransaction) {
        try {
            PaymentTransaction mergedPaymentTransaction = getEntityManager().merge(paymentTransaction);
            getEntityManager().flush();
            return Either.right(mergedPaymentTransaction);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update payment transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PaymentTransaction> findPaymentTransactionByIdAndUserId(UUID paymentTransactionId, UUID userId) {
        try {
            Optional<PaymentTransaction> paymentTransaction = find("id = ?1 and transaction.createdBy.id = ?2", paymentTransactionId, userId).firstResultOptional();
            return paymentTransaction.<Either<RepositoryError, PaymentTransaction>>map(Either::right).orElseGet(() -> Either.left(new RepositoryError.NotFound("Payment transaction not found in due repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Operation failed on finding Payment transaction in repository"));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deletePaymentTransactionById(UUID paymentTransactionId, UUID userId) {
        try {
            Optional<PaymentTransaction> transactionOpt = find("id = ?1 and transaction.createdBy.id = ?2", paymentTransactionId, userId).firstResultOptional();

            if (transactionOpt.isPresent()) {
                PaymentTransaction transaction = transactionOpt.get();
                entityManager.remove(transaction);
                return Either.right(true);
            }

            // No matching transaction found, so nothing was deleted.
            return Either.right(false);

        } catch (Exception e) {
            // Return a more specific error, e.g., if a transaction is still active.
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete payment transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<PaymentTransaction>> findAllPaymentTransactionWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(PaymentTransaction.class)
                    .filter(property -> property.getTransaction().getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<PaymentTransaction> comparator;

                // Determine the comparator based on sortBy
                if ("createdAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PaymentTransactionComparator.BY_CREATED_AT;
                }
                else {
                    return Either.left(new RepositoryError.FetchFailed("Invalid sortBy value: " + query.getSortBy()));
                }

                // Apply ascending or descending order
                String sortDirection = query.getSortDirection();
                if (sortDirection == null || sortDirection.isBlank() || "DESC".equalsIgnoreCase(sortDirection)) {
                    comparator = comparator.reversed(); // Default to DESC
                }
                stream = stream.sorted(comparator);
            }

            int page = query.getPage() != null ? query.getPage() : 0;
            int size = query.getSize() != null ? query.getSize() : 10;

            // Pagination logic
            int skip = page * size;
            if (skip < 0) {
                skip = 0; // Ensure skip is not negative
            }
            if (size <= 0) {
                return Either.left(new RepositoryError.FetchFailed("Size must be greater than zero"));
            }
            List<PaymentTransaction> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find PaymentTransaction" + e.getMessage()));
        }
    }
}
