package contact.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ContactType;
import common.errorStructure.RepositoryError;
import contact.domain.comparator.ContactTypeComparators;
import contact.repository.internal.InternalContactTypeRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@ApplicationScoped
public class ContactTypeRepositoryImpl implements PanacheRepositoryBase<ContactType, UUID>, InternalContactTypeRepository {


    private final JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);


    @Override
    public Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId) {
        try {
            boolean exists = find("detail = ?1 AND createdBy.id = ?2", detail, userId).firstResultOptional().isPresent();
            return Either.right(exists);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to check if detail exists" + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID contactTypeId, UUID userId) {
        try {
            boolean exists = find("id =?1 and createdBy.id = ?2", contactTypeId, userId).firstResultOptional().isPresent();
            return Either.right(exists);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to check if contactTypeId exists" + e.getMessage()));
        }

    }

    @Override
    public Either<RepositoryError, ContactType> createContactType(ContactType contactType) {
        try {
            persist(contactType);
            return Either.right(contactType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to create contactType"));
        }
    }

    @Override
    public Either<RepositoryError, ContactType> updateContactType(ContactType contactType) {
        try {
            ContactType updatedContactType = getEntityManager().merge(contactType);
            return Either.right(updatedContactType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to update contactType"));
        }
    }

    @Override
    public Either<RepositoryError, Optional<ContactType>> findContactTypeAndUserId(UUID contactTypeId, UUID userId) {
        try {
            Optional<ContactType> contactType = find("id = ?1 and createdBy.id = ?2", contactTypeId, userId).firstResultOptional();
            return Either.right(contactType);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to find contactType"));
        }
    }

    @Override
    public Either<RepositoryError, List<ContactType>> findAllContactTypeWithUserId(UUID userId, BaseQuery query) {
        try {

            var stream = jpaStreamer.stream(ContactType.class)
                    .filter(contactType -> contactType.getCreatedBy().getId().equals(userId));

            // apply sorting base on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<ContactType> comparator;

                // Determine the comparator based on sortBy
                if ("detail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ContactTypeComparators.BY_DETAIL;
                } else if ("createAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ContactTypeComparators.BY_CREATED_AT;
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

            // Pagination logic
            int skip = query.getPage() * query.getSize();
            if (skip < 0) {
                skip = 0; // Ensure skip is not negative
            }
            if (query.getSize() <= 0) {
                return Either.left(new RepositoryError.FetchFailed("Size must be greater than zero"));
            }
            List<ContactType> result = stream
                    .skip(skip)
                    .limit(query.getSize())
                    .toList();

            return Either.right(result);

        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Failed to find contactType"));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteContactTypeByIdAndUserId(UUID contactTypeId, UUID userId) {
        try {
            Optional<ContactType> isContactExist = find("id = ?1 and createdBy.id = ?2", contactTypeId, userId).firstResultOptional();
            if (isContactExist.isEmpty()) {
                return Either.right(false);
            }
            delete(isContactExist.get());
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceFailed("Failed to delete contactType"));
        }
    }
}
