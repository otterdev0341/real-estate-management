package sale.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.Property;
import common.domain.entity.SaleTransaction;
import common.errorStructure.RepositoryError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import sale.domain.comparator.SaleTransactionComparators;
import sale.repository.internal.InternalSaleRepository;

import java.util.*;
import java.util.stream.Stream;

@ApplicationScoped
@Named("saleTransactionRepository")
public class SaleTransactionRepositoryImpl implements PanacheRepositoryBase<SaleTransaction, UUID>, InternalSaleRepository, FileAssetManagementRepository {

    @Inject
    EntityManager entityManager;

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);


    @Override
    public Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        try {
            // Find the specific memo first
            Either<RepositoryError, SaleTransaction> saleTransactionExist = findSaleTransactionByIdAndUserId(targetId, userId);
            if (saleTransactionExist.isLeft()) {
                return Either.left(new RepositoryError.NotFound("Sale Transaction not found"));
            }
            SaleTransaction saleTransaction = saleTransactionExist.getRight();

            // Get the files from the properties fileDetails collection
            Set<FileDetail> fileDetails = saleTransaction.getFileDetails();

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
            return Either.left(new RepositoryError.FetchFailed("Failed to fetch file related of transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, SaleTransaction> createNewSaleTransaction(SaleTransaction saleTransaction) {
        try {
            entityManager.persist(saleTransaction);
            return Either.right(saleTransaction);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create sale transaction: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, SaleTransaction> updateSaleTransaction(SaleTransaction saleTransaction) {
        try {
            SaleTransaction mergedSaleTransaction = getEntityManager().merge(saleTransaction);
            getEntityManager().flush();
            return Either.right(mergedSaleTransaction);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update property: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, SaleTransaction> findSaleTransactionByIdAndUserId(UUID saleTransactionId, UUID userId) {
        try {
            Optional<SaleTransaction> saleTransactionOptional = find("id = ?1 and transaction.createdBy.id = ?2", saleTransactionId, userId).firstResultOptional();
            return saleTransactionOptional
                    .<Either<RepositoryError, SaleTransaction>>map(Either::right)
                    .orElse(Either.left(new RepositoryError.NotFound("Sale Transaction not found in due repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Operation failed on finding Property in repository"));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteSaleTransactionByIdAndUserId(UUID saleTransactionId, UUID userId) {
        try {
            Optional<SaleTransaction> saleTransactionOptional = find("id = ?1 and transaction.createdBy.id = ?2", saleTransactionId, userId).firstResultOptional();
            if (saleTransactionOptional.isPresent()) {
                delete(saleTransactionOptional.get());
                return Either.right(true);
            } else {
                return Either.right(false);
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete property: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isSaleTransactionExist(UUID saleTransactionId, UUID userId) {
        try {
            Boolean saleTransactionOptional = find("id = ?1 and transaction.createdBy.id = ?2", saleTransactionId, userId).firstResultOptional().isPresent();
            return Either.right(saleTransactionOptional);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete property: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<SaleTransaction>> findAllSaleTransactionsWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(SaleTransaction.class)
                    .filter(saleTransaction -> saleTransaction.getTransaction().getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<SaleTransaction> comparator;

                // Determine the comparator based on sortBy
                if ("createdAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = SaleTransactionComparators.BY_CREATED_AT;
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
            List<SaleTransaction> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find Property" + e.getMessage()));
        }
    }
}
