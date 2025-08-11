package contact.repository.implementation;

import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Contact;
import common.errorStructure.RepositoryError;
import contact.domain.comparator.ContactComparators;
import contact.repository.internal.InternalContactRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ContactRepositoryImpl implements PanacheRepositoryBase<Contact, UUID>, InternalContactRepository {

    final private JPAStreamer jpaStreamer = JPAStreamer.of(this::getEntityManager);


    @Override
    public Either<RepositoryError, Boolean> isExistByBusinessNameAndUserId(String businessName, UUID userId) {
        try {
            Boolean isBusinessExist = find("businessName = ?1 and createdBy.id = ?2", businessName.trim(), userId)
                    .firstResultOptional()
                    .isPresent();

            return Either.right(isBusinessExist);
        } catch (Exception e)
        {
            return Either.left(new RepositoryError.FetchFailed("Error checking Business Name: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID contactId, UUID userId) {
        try {
            Boolean isContactExist = find("id = ?1 and createdBy.id = ?2", contactId, userId)
                    .firstResultOptional()
                    .isPresent();

            return Either.right(isContactExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error checking contactId: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Contact> createContact(Contact contact) {
        try {
            persist(contact);
            return Either.right(contact);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceError("Error creating new Contact: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Contact> updateContact(Contact contact) {
        try {
            Contact mergedContact = getEntityManager().merge(contact);
            getEntityManager().flush();
            return Either.right(mergedContact);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceError("Error updating Contact: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Optional<Contact>> findContactByIdAndUserId(UUID contactId, UUID userId) {
        try {
            Optional<Contact> isContactExist = find("id = ?1 and createdBy.id = ?2", contactId, userId).firstResultOptional();
            return Either.right(isContactExist);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceError("Error fetching Contact by ID: " + e.getMessage()));
        }
    }

    @Override
    public Either<RepositoryError, Boolean> deleteContact(UUID contactId, UUID userId) {
        try {
            Optional<Contact> isContactExist = find("id = ?1 and createdBy.id = ?2", contactId, userId).firstResultOptional();
            if (isContactExist.isEmpty()) {
                return Either.right(false);
            }
            delete(isContactExist.get());
            return Either.right(true);
        } catch (Exception e) {
            return Either.left(new RepositoryError.PersistenceError("Error deleting Contact: " + e.getMessage()));
        }

    }

    @Override
    public Either<RepositoryError, List<Contact>> findAllContactWithUserId(UUID userId, BaseQuery query) {
        try {
            // Stream all contacts for the user
            var stream = jpaStreamer.stream(Contact.class)
                    .filter(c -> c.getCreatedBy().getId().equals(userId));

            // Apply sorting based on query parameters
            if (query.getSortBy() != null && !query.getSortBy().isBlank()) {
                Comparator<Contact> comparator;

                // Determine the comparator based on sortBy
                if ("businessName".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ContactComparators.BY_BUSINESS_NAME;
                } else if ("createdAt".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ContactComparators.BY_CREATED_AT;
                } else if ("contactTypeDetail".equalsIgnoreCase(query.getSortBy())) {
                    comparator = ContactComparators.BY_CONTACT_TYPE_DETAIL;
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

            // Fetch the results
            List<Contact> contacts = stream.skip(skip).limit(query.getSize()).toList();
            return Either.right(contacts);
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("Error fetching contacts by user ID: " + e.getMessage()));
        }
    }
}
