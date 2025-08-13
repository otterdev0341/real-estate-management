package contact.service.implementation;

import auth.service.declare.DeclareUserService;
import com.speedment.jpastreamer.application.JPAStreamer;
import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.Contact;
import common.domain.entity.ContactType;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import contact.domain.dto.contact.ReqCreateContactDto;
import contact.domain.dto.contact.ReqUpdateContactDto;
import contact.domain.dto.contact.ResEntryContactDto;
import contact.domain.mapper.ContactMapper;
import contact.repository.internal.InternalContactRepository;
import contact.service.declare.DeclareContactService;
import contact.service.declare.DeclareContactTypeService;
import contact.service.internal.InternalContactService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ContactService implements InternalContactService, DeclareContactService {


    public final DeclareUserService userService;
    public final InternalContactRepository contactRepository;
    public final DeclareContactTypeService contactTypeService;
    public final ContactMapper contactMapper;


    @Inject
    public ContactService(
            DeclareUserService userService,
            InternalContactRepository contactRepository,
            DeclareContactTypeService contactTypeService,
            ContactMapper contactMapper
    ) {
        this.userService = userService;
        this.contactRepository = contactRepository;
        this.contactTypeService = contactTypeService;
        this.contactMapper = contactMapper;
    }

    @Override
    public Either<ServiceError, Boolean> isContactExistWithUserId(UUID contactId, UUID userId) {
        return contactRepository.isExistByIdAndUserId(contactId, userId)
                .fold(
                    error -> {
                        ServiceError theError = new ServiceError.OperationFailed("Failed to check if contactId exist reason by :" + error.message());
                        return Either.left(theError);
                    },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Optional<ResEntryContactDto>> findContactByIdAndUserId(UUID contactId, UUID userId) {
        return contactRepository.findContactByIdAndUserId(contactId, userId)
                .fold(
                    error -> {
                        ServiceError theError = new ServiceError.OperationFailed("Failed to fetch contact reason: " + error.message());
                        return Either.left(theError);
                    },
                    success -> {
                        if (success.isEmpty()) {
                            return Either.right(Optional.empty());
                        }
                        ResEntryContactDto theDto = contactMapper.toDto(success.get());
                        return Either.right(Optional.of(theDto));
                    }
                );
    }

    @Override
    public Either<ServiceError, ResEntryContactDto> createNewContact(UUID userId, ReqCreateContactDto reqCreateContactDto) {

        Either<ServiceError, Contact> contactToPersist = validateNewContact(userId, reqCreateContactDto);
        if (contactToPersist.isLeft()) {
            return Either.left(contactToPersist.getLeft());
        }
        Contact contact = contactToPersist.getRight();

        return contactRepository.createContact(contact)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.PersistenceFailed("Failed to crate new contact reason by :" + error.message());
                      return Either.left(theError);
                  },
                  success -> {
                      ResEntryContactDto theDto = contactMapper.toDto(success);
                      return Either.right(theDto);
                  }
                );
    }

    @Override
    public Either<ServiceError, ResEntryContactDto> updateContact(UUID userId, UUID contactId, ReqUpdateContactDto reqUpdateContactDto) {

        Either<ServiceError, Contact> updatedContact = validateUpdateContact(userId, contactId, reqUpdateContactDto);
        if (updatedContact.isLeft()) {
            return Either.left(updatedContact.getLeft());
        }
        Contact contact = updatedContact.getRight();

        return contactRepository.updateContact(contact)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.PersistenceFailed("Failed to update user cause by :" + error.message());
                            return Either.left(theError);
                        },
                        success -> {
                            ResEntryContactDto theDto = contactMapper.toDto(success);
                            return Either.right(theDto);
                        }
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteContact(UUID userId, UUID contactId) {
        return contactRepository.deleteContact(contactId, userId)
                .fold(
                    error -> {
                        ServiceError theError = new ServiceError.OperationFailed("Failed to delete contact reason by" + error.message());
                        return Either.left(theError);
                    },
                    Either::right

                );
    }

    @Override
    public Either<ServiceError, ResListBaseDto<ResEntryContactDto>> findAllContactsByUserId(UUID userId, BaseQuery query) {
        return contactRepository.findAllContactWithUserId(userId, query)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("Failed to fetch contact list reason" + error.message());
                      return Either.left(theError);

                  },
                  success -> {
                      ResListBaseDto<ResEntryContactDto> payload = contactMapper.toResListBaseDto("Contact list fetch successfully", success);
                      return Either.right(payload);
                  }
                );

    }

    @Override
    public Either<ServiceError, Optional<ResEntryContactDto>> findTheContactByIdAndUserId(UUID contactId, UUID userId) {
        return contactRepository.findContactByIdAndUserId(contactId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Failed to find contact by id : " + error.message());
                            return Either.left(theError);
                        },
                        success -> {
                            if (success.isEmpty()) {
                                return Either.right(Optional.empty());
                            }
                            ResEntryContactDto dto = contactMapper.toDto(success.get());
                            return Either.right(Optional.of(dto));

                        }
                );


    }

    private Either<ServiceError, Contact> validateNewContact(UUID userId, ReqCreateContactDto reqCreateContactDto) {
        // section 1 : retrieved user object
        Either<ServiceError, User> targetUser = userService.findUserById(userId);
        // handleFetch error
        if (targetUser.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to fetch user reason by :" + targetUser.getLeft().message()));
        }
        // handle user not found

        User user = targetUser.getRight();

        // section 2 : check is business exist with the user
        Either<RepositoryError, Boolean> businessNameCheck = contactRepository.isExistByBusinessNameAndUserId(reqCreateContactDto.getBusinessName().trim(), userId);
        // handle check error
        if (businessNameCheck.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to check business name exist reason by :" + targetUser.getLeft().message()));
        }
        // handle duplicate
        if (businessNameCheck.getRight()) {
            return Either.left(new ServiceError.DuplicateEntry("The business name already exist with this user, value as" + reqCreateContactDto.getBusinessName()));
        }
        // section 3 : check is contact type exist and belong to user
        Either<ServiceError, ContactType> isContactTypeExist = contactTypeService.findContactTypeByIdAndUserId(reqCreateContactDto.getContactType(), userId);
        // handle check error
        if (isContactTypeExist.isLeft()) {
            return Either.left(isContactTypeExist.getLeft());
        }
        ContactType contactType = isContactTypeExist.getRight();
        // section 4 : prepare new contact
        Contact contact = Contact.builder()
                .businessName(reqCreateContactDto.getBusinessName().trim())
                .internalName(reqCreateContactDto.getInternalName())
                .detail(reqCreateContactDto.getDetail())
                .note(reqCreateContactDto.getNote())
                .contactType(contactType)
                .address(reqCreateContactDto.getAddress().trim())
                .phone(reqCreateContactDto.getPhone())
                .mobilePhone(reqCreateContactDto.getMobilePhone())
                .line(reqCreateContactDto.getLine())
                .email(reqCreateContactDto.getEmail())
                .createdBy(user)
                .build();
        return Either.right(contact);
    }

    private Either<ServiceError, Contact> validateUpdateContact(UUID userId, UUID contactId, ReqUpdateContactDto reqUpdateContactDto) {

        // section 2 : fetch contact by id
        Either<RepositoryError, Optional<Contact>> isContactExist = contactRepository.findContactByIdAndUserId(contactId, userId);
        // handle fetch error
        if (isContactExist.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to fetch contact reason by :" + isContactExist.getLeft().message()));
        }
        // handle not found
        if (isContactExist.getRight().isEmpty()) {
            return Either.left(new ServiceError.NotFound("Contact not found with id : " + contactId.toString() + " under update contact operation"));
        }
        Contact updatedContact = isContactExist.getRight().get();


        // section 3 : check is new businessName didn't exist and not the same as before
        Either<RepositoryError, Boolean> businessNameCheck = contactRepository.isExistByBusinessNameAndUserId(reqUpdateContactDto.getBusinessName().trim(), userId);
        // handle operation error
        if (businessNameCheck.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to check business name exist reason by :" + businessNameCheck.getLeft().message()));
        }
        // handle duplicate
        if (businessNameCheck.getRight()) {
            return Either.left(new ServiceError.DuplicateEntry("The business name already exist with this user, value as" + reqUpdateContactDto.getBusinessName()));
        }
        // handle the new and old is the same
        if (updatedContact.getBusinessName().equals(reqUpdateContactDto.getBusinessName().trim())) {
            return Either.left(new ServiceError.ValidationFailed("Conflict Error, the old Business Name is: " + updatedContact.getBusinessName().trim() + " the new to update is :" + reqUpdateContactDto.getBusinessName().trim()));
        }

        // section 4 : check is contact type exist and belong to user
        Either<ServiceError, ContactType> isContactTypeExist = contactTypeService.findContactTypeByIdAndUserId(reqUpdateContactDto.getContactType(), userId);
        // handle fetch error
        if (isContactTypeExist.isLeft()) {
            return Either.left(isContactTypeExist.getLeft());
        }

        ContactType contactType = isContactTypeExist.getRight();

        // section 5 : return entity to persist
        updatedContact.setBusinessName(reqUpdateContactDto.getBusinessName().trim());
        updatedContact.setInternalName(reqUpdateContactDto.getInternalName());
        updatedContact.setDetail(reqUpdateContactDto.getDetail());
        updatedContact.setNote(reqUpdateContactDto.getNote());
        updatedContact.setContactType(contactType);
        updatedContact.setAddress(reqUpdateContactDto.getAddress().trim());
        updatedContact.setPhone(reqUpdateContactDto.getPhone());
        updatedContact.setMobilePhone(reqUpdateContactDto.getMobilePhone());
        updatedContact.setLine(reqUpdateContactDto.getLine());
        updatedContact.setEmail(reqUpdateContactDto.getEmail());
        return Either.right(updatedContact);

    }

}
