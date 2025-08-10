package contact.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.base.ResListBaseDto;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.ContactType;
import common.domain.entity.User;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import contact.domain.dto.contactType.ReqCreateContactTypeDto;
import contact.domain.dto.contactType.ReqUpdateContactTypeDto;
import contact.domain.dto.contactType.ResEntryContactTypeDto;
import contact.domain.mapper.ContactTypeMapper;
import contact.repository.internal.InternalContactTypeRepository;
import contact.service.declare.DeclareContactTypeService;
import contact.service.internal.InternalContactTypeService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ContactTypeService implements InternalContactTypeService, DeclareContactTypeService {

    private final DeclareUserService userService;
    private final InternalContactTypeRepository contactTypeRepository;
    private final ContactTypeMapper contactTypeMapper;

    @Inject
    public ContactTypeService(DeclareUserService userService, InternalContactTypeRepository contactTypeRepository, ContactTypeMapper contactTypeMapper) {
        this.userService = userService;
        this.contactTypeRepository = contactTypeRepository;
        this.contactTypeMapper = contactTypeMapper;
    }

    @Override
    public Either<ServiceError, Boolean> isContactTypeExistWithUserId(UUID contactTypeId, UUID userId) {
        return contactTypeRepository.isExistByIdAndUserId(contactTypeId, userId)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("Failed to check if contactTypeId exist" + error.message());
                      return Either.left(theError);
                  },
                  Either::right
                );
    }

    @Override
    public Either<ServiceError, Optional<ContactType>> findContactTypeByIdAndUserId(UUID contactTypeId, UUID userId) {
        return contactTypeRepository.findContactTypeAndUserId(contactTypeId, userId)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("Failed to retrieve contact type reason by :" + error.message());
                      return Either.left(theError);
                  },
                  Either::right
                );
    }

    @Override
    public Either<ServiceError, ResEntryContactTypeDto> createNewContactType(UUID userId, ReqCreateContactTypeDto reqCreateContactTypeDto) {

        return userService.findUserById(userId)
                .flatMapLeft(Either::left) // ถ้า error ให้ส่งต่อเลย
                .flatMapRight(optUser -> {
                    if (optUser.isEmpty()) {
                        return Either.left(new ServiceError.NotFound("User not found with id : " + userId));
                    }

                    User user = optUser.get();

                    return contactTypeRepository.isExistByDetailAndUserId(
                                    reqCreateContactTypeDto.getDetail().trim(),
                                    userId
                            )
                            // ผูก user กับ isExist เข้า Pair เดียวกัน
                            .mapRight(isExist -> Pair.of(user, isExist))
                            .mapLeft(repoErr -> new ServiceError.ValidationFailed(repoErr.message()));
                })
                .flatMapRight(pair -> {
                    User user = pair.getLeft();
                    Boolean isExist = pair.getRight();

                    if (isExist) {
                        return Either.left(new ServiceError.DuplicateEntry(
                                "Contact type already exists for this user"
                        ));
                    }
                    if (user == null) {
                        // without this check it will error
                        return Either.left(new ServiceError.OperationFailed("created contact user still be null"));
                    }

                    ContactType newContactType = ContactType.builder()
                            .detail(reqCreateContactTypeDto.getDetail())
                            .createdBy(user)
                            .build();

                    return contactTypeRepository.createContactType(newContactType)
                            .mapRight(contactTypeMapper::toDto)
                            .mapLeft(err -> new ServiceError.OperationFailed(
                                    "Can't persist new contact type: " + err.message()
                            ));
                });

        /*
        // get user object
        Either<ServiceError, Optional<User>> targetUser = userService.findUserById(userId);
        // error fetch user
        if (targetUser.isLeft()) {
            ServiceError theError = new ServiceError
                    .ValidationFailed("User not found with id : " + userId.toString() + " under create new contact type operation." + " cause by " + targetUser.getLeft().message());
            return Either.left(theError);
        }
        // error user not found
        if (targetUser.getRight().isEmpty()) {
            ServiceError theError = new ServiceError.NotFound("User not found with id : " + userId.toString() + " under create new contact type operation");
            return Either.left(theError);

        }
        User theUser = targetUser.getRight().get();
        // check is the detail is not exist with this user
        Either<RepositoryError, Boolean> isContactTypeExist = contactTypeRepository.isExistByDetailAndUserId(reqCreateContactTypeDto.getDetail().trim(), userId);
        // handle fetch error
        if (isContactTypeExist.isLeft()) {
            ServiceError theError = new ServiceError.ValidationFailed("Failed to check contact type detail, is exist before insert" + isContactTypeExist.getLeft().message());
            return Either.left(theError);
        }
        // handle duplicate
        if (isContactTypeExist.getRight()) {
            ServiceError theError = new ServiceError.DuplicateEntry("The contact type detail already exist with this user by value of :" + reqCreateContactTypeDto.getDetail().trim());
            return Either.left(theError);
        }
        // create new and persist
        ContactType newContactType = ContactType.builder()
                .detail(reqCreateContactTypeDto.getDetail())
                .createdBy(theUser)
                .build();
        return contactTypeRepository.createContactType(newContactType)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Can't persist new contact type reason by" + error.message());
                            return Either.left(theError);
                        },
                        success -> {
                            ResEntryContactTypeDto resEntryContactTypeDto = contactTypeMapper.toDto(success);
                            return Either.right(resEntryContactTypeDto);
                        }
                );
    */

    }

    @Override
    public Either<ServiceError, ResEntryContactTypeDto> updateContactType(UUID userId, UUID contactTypeId, ReqUpdateContactTypeDto reqUpdateContactTypeDto) {
        // is contact type exist
        Either<RepositoryError, Optional<ContactType>> isContactTypeExist = contactTypeRepository.findContactTypeAndUserId(contactTypeId, userId);
        if (isContactTypeExist.isLeft()) {
            ServiceError theError = new ServiceError.OperationFailed("Failed to fetch contact type reason by " + isContactTypeExist.getLeft().message());
            return Either.left(theError);
        }
        if (isContactTypeExist.getRight().isEmpty()){
            ServiceError theError = new ServiceError.NotFound("Contact type not found with id:" + contactTypeId);
            return Either.left(theError);
        }
        ContactType theContactType = isContactTypeExist.getRight().get();
        // is new contact type detail exist and not the same as founded
        Either<RepositoryError, Boolean> isNewNameExist = contactTypeRepository.isExistByDetailAndUserId(reqUpdateContactTypeDto.getDetail().trim(), userId);
        if (isNewNameExist.isLeft()) {
            ServiceError theError = new ServiceError.OperationFailed("Failed to check new contact type");
            return Either.left(theError);
        }
        if (isNewNameExist.getRight() || theContactType.getDetail().equalsIgnoreCase(reqUpdateContactTypeDto.getDetail().trim())) {
            ServiceError theError = new ServiceError.DuplicateEntry("The contact type detail already exist with this user, the new detail to update is exist");
            return Either.left(theError);
        }
        // persis update
        return contactTypeRepository.updateContactType(theContactType)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("Failed to update contact type with : " + reqUpdateContactTypeDto.getDetail() + " cause by: " + error.message());
                      return Either.left(theError);
                  },
                  success -> {
                      ResEntryContactTypeDto result = contactTypeMapper.toDto(success);
                      return Either.right(result);
                  }
                );
    }

    @Override
    public Either<ServiceError, Boolean> deleteContactType(UUID userId, UUID contactTypeId) {
        return contactTypeRepository.deleteContactTypeByIdAndUserId(contactTypeId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.PersistenceFailed(
                                    "Failed to delete contact type reason by: " + error.message()
                            );
                            return Either.left(theError);
                        },
                        Either::right

                );
    }

    @Override
    public Either<ServiceError, ResListBaseDto<ResEntryContactTypeDto>> findAllContactTypesByUserId(UUID userId, BaseQuery query) {
        return contactTypeRepository.findAllContactTypeWithUserId(userId, query)
                .fold(
                    error -> {
                        ServiceError theError = new ServiceError.OperationFailed("Failed to fetch contact type list reason by :" + error.message());
                        return Either.left(theError);
                    },
                    success -> {
                        ResListBaseDto<ResEntryContactTypeDto> payload = contactTypeMapper.toResListBaseDto("Contact type list fetch successfully", success);
                        return Either.right(payload);
                    }
                );
    }

    @Override
    public Either<ServiceError, Optional<ResEntryContactTypeDto>> findTheContactTypeByIdAndUserId(UUID contactTypeId, UUID userId) {
        return contactTypeRepository.findContactTypeAndUserId(contactTypeId, userId)
                .fold (
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("Failed to fetch contact type reason" + error.message());
                      return Either.left(theError);
                  },
                  success -> {
                      if (success.isEmpty()) {
                          return Either.right(Optional.empty());
                      }
                      ResEntryContactTypeDto payload = contactTypeMapper.toDto(success.get());
                      return Either.right(Optional.of(payload));
                  }
                );
    }
}
