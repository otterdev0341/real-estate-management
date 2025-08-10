package contact.repository.internal;

import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ContactType;
import common.errorStructure.RepositoryError;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InternalContactTypeRepository {

    Either<RepositoryError, Boolean> isExistByDetailAndUserId(String detail, UUID userId);

    Either<RepositoryError, Boolean> isExistByIdAndUserId(UUID contactTypeId, UUID userId);

    Either<RepositoryError, ContactType> createContactType(ContactType contactType);

    Either<RepositoryError, ContactType> updateContactType(ContactType contactType);

    Either<RepositoryError, Optional<ContactType>> findContactTypeAndUserId(UUID contactTypeId, UUID userId);

    Either<RepositoryError, List<ContactType>> findAllContactTypeWithUserId(UUID userId, BaseQuery query);

    Either<RepositoryError, Boolean> deleteContactTypeByIdAndUserId(UUID contactTypeId, UUID userId);

}
