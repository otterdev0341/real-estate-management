package property.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.FileDetail;
import common.domain.entity.Memo;
import common.domain.entity.Property;
import common.errorStructure.RepositoryError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import memo.domain.comparator.MemoComparator;
import property.domain.comparator.PropertyComparators;
import property.repository.internal.InternalPropertyRepository;

import java.util.*;
import java.util.stream.Stream;

@ApplicationScoped
@Named("propertyRepository")
public class PropertyRepositoryImpl implements PanacheRepositoryBase<Property, UUID>, InternalPropertyRepository, FileAssetManagementRepository {
    @Inject
    EntityManager entityManager;

    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);

    @Override
    public Either<RepositoryError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        try {
            // Find the specific memo first
            Either<RepositoryError, Property> propertyExist = findPropertyByIdAndUserId(targetId, userId);
            if (propertyExist.isLeft()) {
                return Either.left(new RepositoryError.NotFound("Property not found"));
            }
            Property property = propertyExist.getRight();

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
    public Either<RepositoryError, Boolean> isExistByNameAndUserId(String name, UUID userId) {
        try {
            Boolean isExist = find("name = ?1 and createdBy.id = ?2", name.trim(), userId).firstResultOptional().isPresent();
            return Either.right(isExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by name and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID propertyId, UUID userId) {
        try {
            Boolean isExist = find("id = ?1 and createdBy.id = ?2", propertyId, userId).firstResultOptional().isPresent();
            return Either.right(isExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking existence by ID and user ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Property> createProperty(Property property) {
        try {
            entityManager.persist(property);
            return Either.right(property);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create property: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Property> updateProperty(Property property) {
        try {
            Property mergedProperty = getEntityManager().merge(property);
            getEntityManager().flush();
            return Either.right(mergedProperty);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update property: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Property> findPropertyByIdAndUserId(UUID propertyId, UUID userId) {
        try {
            Optional<Property> property = find("id = ?1 and createdBy.id = ?2", propertyId, userId).firstResultOptional();
            return property.<Either<RepositoryError, Property>>map(Either::right).orElseGet(() -> Either.left(new RepositoryError.NotFound("Property not found in due repository")));
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Operation failed on finding Property in repository"));
        }
    }

    @Override
    public Either<RepositoryError, List<Property>> findAllPropertyWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(Property.class)
                    .filter(property -> property.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<Property> comparator;

                // Determine the comparator based on sortBy
                if ("sold".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyComparators.BY_SOLD_STATUS;
                } else if ("fsp".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyComparators.BY_FSP;
                } else if ("propertyStatus".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyComparators.BY_PROPERTY_STATUS;
                } else if ("owner".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyComparators.BY_OWNER;
                } else if ("name".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyComparators.BY_Name;
                } else if ("createdAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = PropertyComparators.BY_CREATED_AT;
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
            List<Property> result = stream
                    .skip(skip)
                    .limit(size)
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find Property" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deletePropertyByIdAndUserId(UUID propertyId, UUID userId) {
        try {
            Optional<Property> property = find("id = ?1 and createdBy.id = ?2", propertyId, userId).firstResultOptional();
            if (property.isPresent()) {
                delete(property.get());
                return Either.right(true);
            } else {
                return Either.right(false);
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete property: " + e.getMessage()));
        }
    }
}
