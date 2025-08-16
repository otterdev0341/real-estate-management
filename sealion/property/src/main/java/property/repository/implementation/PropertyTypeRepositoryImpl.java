package property.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.PropertyType;
import common.errorStructure.RepositoryError;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import property.domain.comparator.PropertyTypeComparators;
import property.repository.internal.InternalPropertyTypeRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PropertyTypeRepositoryImpl implements PanacheRepositoryBase<PropertyType, UUID>, InternalPropertyTypeRepository {

    final private JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId) {
        try {
            Boolean isPropertyTypeDetailExist = find("detail = ?1 and createdBy.id =?2", detail.trim(), userId).firstResultOptional().isPresent();
            return Either.right(isPropertyTypeDetailExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by detail and user ID" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID propertyTypeId, UUID userId) {
        try {
            Boolean isPropertyTypeExist = find("id = ?1 and createdBy.id = ?2", propertyTypeId, userId).firstResultOptional().isPresent();
            return Either.right(isPropertyTypeExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PropertyType> createPropertyType(PropertyType propertyType) {
        try {
            persist(propertyType);
            return Either.right(propertyType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error creating PropertyType: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PropertyType> updatePropertyType(PropertyType propertyType) {
        try {
            PropertyType mergedPropertyType = getEntityManager().merge(propertyType);
            getEntityManager().flush();
            return Either.right(mergedPropertyType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error updating Property Type: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, PropertyType> findPropertyTypeByIdAndUserId(UUID propertyTypeId, UUID userId) {
        try {
            Optional<PropertyType> propertyTypeOptional = find("id = ?1 and createdBy.id = ?2", propertyTypeId, userId).firstResultOptional();
            return propertyTypeOptional
                    .<Either<RepositoryError, PropertyType>>map(Either::right)
                    .orElseGet(() -> Either.left(new RepositoryError.NotFound("Property Type not found for ID: " + propertyTypeId)));
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error finding Property Type by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deletePropertyTypeByIdAndUserId(UUID propertyTypeId, UUID userId) {
        try {
            Optional<PropertyType> propertyTypeOptional = find("id = ?1 and createdBy.id = ?2", propertyTypeId, userId).firstResultOptional();
            if (propertyTypeOptional.isEmpty()) {
                return Either.right(false);
            }
            delete(propertyTypeOptional.get());
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Error deleting Property Type by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, List<PropertyType>> findAllPropertyTypeWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(PropertyType.class)
                    .filter(propertyType -> propertyType.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<PropertyType> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyTypeComparators.BY_DETAIL;
                } else if ("createAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyTypeComparators.BY_CREATED_AT;
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
            List<PropertyType> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find property Type" + e.getMessage()));
        }
    }
}
