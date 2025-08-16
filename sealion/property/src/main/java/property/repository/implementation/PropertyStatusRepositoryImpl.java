package property.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyStatus;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import property.domain.comparator.PropertyStatusComparators;
import property.repository.internal.InternalPropertyStatusRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
public class PropertyStatusRepositoryImpl implements PanacheRepositoryBase<PropertyStatus, UUID>, InternalPropertyStatusRepository {

    final private JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId) {
        try {
            Boolean isPropertyStatusExist = find("detail = ?1 and createdBy.id = ?2", detail, userId).firstResultOptional().isPresent();
            return Either.right(isPropertyStatusExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by detail and user ID" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID propertyStatusId, UUID userId) {
        try {
            Boolean isPropertyStatusExist = find("id = ?1 and createdBy.id = ?2", propertyStatusId, userId).firstResultOptional().isPresent();
            return Either.right(isPropertyStatusExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PropertyStatus> createPropertyStatus(PropertyStatus propertyStatus) {
        try {
            persist(propertyStatus);
            return Either.right(propertyStatus);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error creating PropertyStatus: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PropertyStatus> updatePropertyStatus(PropertyStatus propertyStatus) {
        try {
            PropertyStatus mergedPropertyStatus = getEntityManager().merge(propertyStatus);
            getEntityManager().flush();
            return Either.right(mergedPropertyStatus);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error updating PropertyStatus: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PropertyStatus> findPropertyStatusByIdAndUserId(UUID propertyStatusId, UUID userId) {
        try {
         Optional<PropertyStatus> propertyStatusOptional = find("id = ?1 and createdBy.id = ?2", propertyStatusId, userId).firstResultOptional();
            return propertyStatusOptional
                    .<Either<RepositoryError, PropertyStatus>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("PropertyStatus not found for ID: " + propertyStatusId)));
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error finding PropertyStatus by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<PropertyStatus>> findAllPropertyStatusWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(PropertyStatus.class)
                    .filter(propertyStatus -> propertyStatus.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<PropertyStatus> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyStatusComparators.BY_DETAIL;
                } else if ("createAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyStatusComparators.BY_CREATED_AT;
                } else {
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
            List<PropertyStatus> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find property Status" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deletePropertyStatusByIdAndUserId(UUID propertyStatusId, UUID userId) {
        try {
            Optional<PropertyStatus> propertyStatusOptional = find("id = ?1 and createdBy.id = ?2", propertyStatusId, userId).firstResultOptional();
            if (propertyStatusOptional.isEmpty()) {
                return Either.right(false);
            }
            delete(propertyStatusOptional.get());
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error deleting PropertyStatus by ID and user ID: " + e.getMessage()));
        }
    }
}
