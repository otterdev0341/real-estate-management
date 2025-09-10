package investment.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.investment.InvestmentTransaction;
import common.domain.entity.payment.PaymentTransaction;
import common.errorStructure.RepositoryError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import investment.domain.comparator.InvestmentTransactionComparator;
import investment.repository.internal.InternalInvestmentTransactionRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.stream.Stream;

@ApplicationScoped
@Named("investmentRepository")
public class InvestmentRepositoryImpl implements PanacheRepositoryBase<InvestmentTransaction, UUID>, InternalInvestmentTransactionRepository, FileAssetManagementRepository {

    @Inject
    EntityManager entityManager;

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        try {
            // Find the specific memo first
            Either<RepositoryError, InvestmentTransaction> investmentTransactionEither = findInvestmentTransactionByIdAndUserId(targetId, userId);
            if (investmentTransactionEither.isLeft()) {
                return Either.left(new RepositoryError.NotFound("Investment transaction not found"));
            }
            InvestmentTransaction property = investmentTransactionEither.getRight();

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
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch file related of investment: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, InvestmentTransaction> createNewInvestmentTransaction(InvestmentTransaction investmentTransaction) {
        try {
            entityManager.persist(investmentTransaction);
            return Either.right(investmentTransaction);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create Investment Transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, InvestmentTransaction> updateInvestmentTransaction(InvestmentTransaction investmentTransaction) {
        try {
            InvestmentTransaction mergedPaymentTransaction = getEntityManager().merge(investmentTransaction);
            getEntityManager().flush();
            return Either.right(investmentTransaction);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update investment transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, InvestmentTransaction> findInvestmentTransactionByIdAndUserId(UUID investmentTransactionId, UUID userId) {
        try {
            Optional<InvestmentTransaction> paymentTransaction = find("id = ?1 and transaction.createdBy.id = ?2", investmentTransactionId, userId).firstResultOptional();
            return paymentTransaction.<Either<RepositoryError, InvestmentTransaction>>map(Either::right).orElseGet(() -> Either.left(new RepositoryError.NotFound("Investment transaction not found in due repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Operation failed on finding Investment transaction in repository"));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteInvestmentTransactionById(UUID investmentTransactionId, UUID userId) {
        try {
            Optional<InvestmentTransaction> investmentOpt = find("id = ?1 and transaction.createdBy.id = ?2", investmentTransactionId, userId).firstResultOptional();

            if (investmentOpt.isPresent()) {
                InvestmentTransaction transaction = investmentOpt.get();
                entityManager.remove(transaction);
                return Either.right(true);
            }

            // No matching transaction found, so nothing was deleted.
            return Either.right(false);

        } catch (Exception e) {
            // Return a more specific error, e.g., if a transaction is still active.
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete investment transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<InvestmentTransaction>> findAllInvestmentTransactionWIthUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(InvestmentTransaction.class)
                    .filter(investmentTransaction -> investmentTransaction.getTransaction().getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<InvestmentTransaction> comparator;

                // Determine the comparator based on sortBy
                if ("createdAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = InvestmentTransactionComparator.BY_CREATED_AT;
                }
                else if ("propertyId".equalsIgnoreCase(query.getSortBy())) {
                    comparator = InvestmentTransactionComparator.BY_PROPERTY_ID;
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
            List<InvestmentTransaction> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find InvestmentTransaction" + e.getMessage()));
        }
    }
}
