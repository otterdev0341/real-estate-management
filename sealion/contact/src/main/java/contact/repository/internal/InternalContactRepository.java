package contact.repository.internal;

import com.spencerwi.either.Either;
import common.domain.entity.Contact;
import common.errorStructure.RepositoryError;
import contact.domain.dto.query.ContactQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InternalContactRepository {

    Either<RepositoryError, Boolean> isExistByBusinessNameAndUserId(String businessName, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID contactId, UUID userId);

    Either<RepositoryError, Contact> createContact(Contact contact);

    Either<RepositoryError, Contact> updateContact(Contact contact);

    Either<RepositoryError, Optional<Contact>> findContactByIdAndUserId(UUID contactId, UUID userId);

    Either<RepositoryError, Boolean> deleteContact(UUID contactId, UUID userId);

    Either<RepositoryError, List<Contact>> findAllContactWithUserId(UUID userId, ContactQuery query);

}
