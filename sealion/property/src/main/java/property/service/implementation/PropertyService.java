package property.service.implementation;

import auth.service.declare.DeclareUserService;
import com.spencerwi.either.Either;
import common.domain.dto.query.BaseQuery;
import common.domain.entity.*;
import common.errorStructure.RepositoryError;
import common.errorStructure.ServiceError;
import common.repository.declare.FileAssetManagementRepository;
import common.service.declare.fileAssetManagement.FileAssetManagementService;
import common.service.declare.fileAssetManagement.fileAssetChoice.FileCaseSelect;
import contact.domain.dto.contact.ResEntryContactDto;
import contact.service.declare.DeclareContactService;
import fileDetail.service.declare.DeclareFileDetailService;
import io.smallrye.mutiny.tuples.Tuple3;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;
import memo.service.declare.DeclareMemoService;
import org.apache.commons.lang3.tuple.Pair;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import property.domain.dto.property.ReqCreatePropertyDto;
import property.domain.dto.property.ReqUpdatePropertyDto;
import property.repository.internal.InternalPropertyRepository;
import property.repository.internal.InternalPropertyStatusRepository;
import property.repository.internal.InternalPropertyTypeRepository;
import property.service.cross.InternalMemoCrossPropertyService;
import property.service.declare.DeclarePropertyService;
import property.service.internal.InternalPropertyService;
import property.service.internal.InternalPropertyTypeService;

import java.util.*;

@ApplicationScoped
@Named("propertyService")
public class PropertyService implements DeclarePropertyService, InternalPropertyService, FileAssetManagementService, InternalMemoCrossPropertyService {

    private final InternalPropertyRepository propertyRepository;
    private final InternalPropertyStatusRepository propertyStatusRepository;
    private final DeclareFileDetailService fileDetailService;
    private final FileAssetManagementRepository fileAssetManagementRepository;
    private final DeclareContactService contactService;
    private final DeclareUserService userService;
    private final InternalPropertyTypeRepository propertyTypeRepository;
    private final DeclareMemoService memoService;

    @Inject
    public PropertyService(
            @Named("propertyRepository") InternalPropertyRepository propertyRepository,
            @Named("propertyRepository") FileAssetManagementRepository fileAssetManagementRepository,
            InternalPropertyStatusRepository propertyStatusRepository,
            DeclareFileDetailService fileDetailService,
            DeclareContactService contactService,
            DeclareUserService userService,
            InternalPropertyTypeRepository propertyTypeRepository,
            DeclareMemoService memoService
    ) {
        this.propertyRepository = propertyRepository;
        this.propertyStatusRepository = propertyStatusRepository;
        this.fileDetailService = fileDetailService;
        this.fileAssetManagementRepository = fileAssetManagementRepository;
        this.contactService = contactService;
        this.userService = userService;
        this.propertyTypeRepository = propertyTypeRepository;
        this.memoService = memoService;

    }

    @Override
    public Either<ServiceError, Boolean> attachFileToTarget(UUID targetId, UUID userId, FileUpload targetFile) {
        // find memo target
        // update file
        // attached to memo
        if (targetFile == null) {
            ServiceError theError = new ServiceError.ValidationFailed("File can't be null to perform attach file operation");
            return Either.left(theError);
        }

        return propertyRepository.findPropertyByIdAndUserId(targetId, userId)
                .flatMap(error -> {
                            ServiceError theError = new ServiceError.NotFound(error.message());
                            return Either.left(theError);
                        },
                        property -> {
                            return fileDetailService.createFileDetail(targetFile, userId)
                                    .flatMap(
                                            Either::left,
                                            fileDetail -> {
                                                property.addFileDetail(fileDetail);
                                                return propertyRepository.updateProperty(property)
                                                        .flatMap(error -> {
                                                            ServiceError theError = new ServiceError.OperationFailed("can't update file cause by: " + error.message());
                                                            return Either.left(theError);
                                                        }, successOperation -> Either.right(true));
                                            });
                        });

    }

    @Override
    public Either<ServiceError, Boolean> deleteFileByTargetAndFileId(UUID targetId, UUID userId, UUID fileId) {
        // check is property exist
        // check is file exist
        // perform delete
        return propertyRepository.findPropertyByIdAndUserId(targetId, userId)
                .flatMap(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Property not found" + error.message());
                            return Either.left(theError);
                        },
                        property -> {
                            return fileDetailService.findFileDetailAndUserId(fileId, userId)
                                    .flatMap(
                                            Either::left,
                                            fileDetail -> {
                                                boolean isPropertyContainFile = property.getFileDetails().contains(fileDetail);
                                                if (!isPropertyContainFile) {
                                                    ServiceError theError = new ServiceError.BusinessRuleFailed("File does not belong to property");
                                                    return Either.left(theError);
                                                }
                                                property.removeFileDetail(fileDetail);

                                                return propertyRepository.updateProperty(property)
                                                        .flatMap(
                                                                error -> {
                                                                    ServiceError theError = new ServiceError.PersistenceFailed("Can't update property cause by: " + error.message());
                                                                    return Either.left(theError);
                                                                },
                                                                success -> Either.right(true)
                                                        );
                                            }
                                    );
                        }
                );
    }

    @Override
    public Either<ServiceError, List<FileDetail>> getAllFileByCriteria(UUID targetId, UUID userId, FileCaseSelect fileCase) {
        return fileAssetManagementRepository.getAllFileByCriteria(targetId, userId, fileCase)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error occurred while fetching all file relate to property, cause by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> isPropertyExistWithUserId(UUID propertyId, UUID userId) {
        return propertyRepository.isExistByIdAndUserId(propertyId, userId)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error occurred while check is property exist or not, cause by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Property> findPropertyByIdAndUserId(UUID propertyId, UUID userId) {
        return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                .fold(
                  error -> {
                      ServiceError theError = new ServiceError.OperationFailed("Error occurred while fetch property by id, cause by: " + error.message());
                      return Either.left(theError);
                  },
                  Either::right
                );
    }

    @Transactional
    @Override
    public Either<ServiceError, Property> createNewProperty(ReqCreatePropertyDto reqCreatePropertyDto, UUID userId) {
        // check is <User> exist: get user object
        // check is <Boolean>new property name exist
        // check is <PropertyStatus> exist: get object
        // check is owner <Contact> exist: get object
        // prepare property payload
        // persist new entity
        // persist file upload and associate with the property
        Either<ServiceError, User> isUserExist = userService.findUserById(userId);
        if (isUserExist.isLeft()) {
            return Either.left(isUserExist.getLeft());
        }
        User user = isUserExist.getRight();

        Either<RepositoryError, Boolean> isNewPropertyNameExist = propertyRepository.isExistByNameAndUserId(reqCreatePropertyDto.getName().trim(), userId);
        if (isNewPropertyNameExist.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to check is property name exist cause by:" + isNewPropertyNameExist.getLeft().message()));
        }
        if (isNewPropertyNameExist.getRight()) {
            return Either.left(new ServiceError.DuplicateEntry("the property name already exist"));
        }

        Either<RepositoryError, PropertyStatus> isPropertyStatusExist = propertyStatusRepository.findPropertyStatusByIdAndUserId(reqCreatePropertyDto.getPropertyStatus(), userId);
        if (isPropertyStatusExist.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to check is property status exist, cause by:" + isPropertyStatusExist.getLeft().message()));
        }
        PropertyStatus propertyStatus = isPropertyStatusExist.getRight();

        Either<ServiceError, Contact> isOwnerExist = contactService.findContactByIdAndUser(reqCreatePropertyDto.getOwnerBy(), userId);
        if (isOwnerExist.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to check is owner exist, cause by:" + isOwnerExist.getLeft().message()));
        }

        Contact owner = isOwnerExist.getRight();



        Property theProperty = Property.builder()
                .name(reqCreatePropertyDto.getName().trim())
                .description(reqCreatePropertyDto.getDescription())
                .specific(reqCreatePropertyDto.getSpecific())
                .highlight(reqCreatePropertyDto.getHighlight())
                .area(reqCreatePropertyDto.getArea())
                .price(reqCreatePropertyDto.getPrice())
                .fsp(reqCreatePropertyDto.getFsp())
                .budget(reqCreatePropertyDto.getBudget())
                .status(propertyStatus)
                .ownerBy(owner)
                .mapUrl(reqCreatePropertyDto.getMapUrl())
                .lat(reqCreatePropertyDto.getLat())
                .lng(reqCreatePropertyDto.getLng())
                .createdBy(user)
                .sold(false)
                .fileDetails(new HashSet<>())
                .build();

        for (FileUpload file : reqCreatePropertyDto.getFiles()) {
            Either<ServiceError, FileDetail> fileDetailPreload = fileDetailService.helpPrePersistFileDetail(file, userId);
            if (fileDetailPreload.isLeft()) {
                return Either.left(fileDetailPreload.getLeft());
            }

            theProperty.addFileDetail(fileDetailPreload.getRight());

        }


        Either<RepositoryError, Property> isPersistNewProperty = propertyRepository.createProperty(theProperty);
        if (isPersistNewProperty.isLeft()) {
            return Either.left(new ServiceError.OperationFailed("Failed to persist new property, cause by:" + isPersistNewProperty.getLeft().message()));
        }

        return Either.right(isPersistNewProperty.getRight());



    } // end create property

    @Override
    public Either<ServiceError, Property> updateProperty(ReqUpdatePropertyDto reqUpdatePropertyDto, UUID propertyId, UUID userId) {
        // check is <Property> exist: get object
        // check is <Boolean> new property name exist
        // check current property name not match the new property name
        // check is <PropertyStatus> exist: get object
        // check is owner <Contact> exist: get object
        // prepare property payload
        // persist new entity
        return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                .mapLeft(error -> (ServiceError) new ServiceError.OperationFailed("Error while fetch property by id, cause by: " + error.message()))
                .flatMapRight(propertyToUpdate -> {
                    String newName = reqUpdatePropertyDto.getName().trim();
                    // Only check for name duplication if the name is being changed.
                    if (!propertyToUpdate.getName().equals(newName)) {
                        Either<RepositoryError, Boolean> isNameExistCase = propertyRepository.isExistByNameAndUserId(newName, userId);
                        if (isNameExistCase.isLeft()) {
                            return Either.left(new ServiceError.OperationFailed("Failed to check for property name existence: " + isNameExistCase.getLeft().message()));
                        }
                        if (isNameExistCase.getRight()) {
                            return Either.left(new ServiceError.DuplicateEntry("The property name '" + newName + "' already exists."));
                        }
                        propertyToUpdate.setName(newName);
                    }

                    // Fetch other related entities for the update.
                    Either<RepositoryError, PropertyStatus> statusCase = propertyStatusRepository.findPropertyStatusByIdAndUserId(reqUpdatePropertyDto.getPropertyStatus(), userId);
                    if (statusCase.isLeft()) {
                        return Either.left(new ServiceError.OperationFailed("Error finding property status: " + statusCase.getLeft().message()));
                    }

                    Either<ServiceError, Contact> ownerCase = contactService.findContactByIdAndUser(reqUpdatePropertyDto.getOwnerBy(), userId);
                    if (ownerCase.isLeft()) {
                        return Either.left(new ServiceError.OperationFailed("Error finding owner: " + ownerCase.getLeft().message()));
                    }

                    // Apply all updates to the property entity.
                    propertyToUpdate.setDescription(reqUpdatePropertyDto.getDescription());
                    propertyToUpdate.setSpecific(reqUpdatePropertyDto.getSpecific());
                    propertyToUpdate.setHighlight(reqUpdatePropertyDto.getHighlight());
                    propertyToUpdate.setArea(reqUpdatePropertyDto.getArea());
                    propertyToUpdate.setPrice(reqUpdatePropertyDto.getPrice());
                    propertyToUpdate.setFsp(reqUpdatePropertyDto.getFsp());
                    propertyToUpdate.setBudget(reqUpdatePropertyDto.getBudget());
                    propertyToUpdate.setStatus(statusCase.getRight());
                    propertyToUpdate.setOwnerBy(ownerCase.getRight());
                    propertyToUpdate.setMapUrl(reqUpdatePropertyDto.getMapUrl());
                    propertyToUpdate.setLat(reqUpdatePropertyDto.getLat());
                    propertyToUpdate.setLng(reqUpdatePropertyDto.getLng());

                    return propertyRepository.updateProperty(propertyToUpdate)
                            .mapLeft(error -> new ServiceError.OperationFailed("Error occurred while updating property: " + error.message()));
                });
    }

    @Override
    public Either<ServiceError, Boolean> deleteProperty(UUID propertyId, UUID userId) {
        return helperDeletePropertyWithRelationFile(propertyId, userId)
                .fold(
                        Either::left,
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, List<Property>> findAllProperties(UUID userId, BaseQuery query) {
        return propertyRepository.findAllPropertyWithUserId(userId, query)
                .fold(
                        error -> {
                            ServiceError theError = new ServiceError.OperationFailed("Error occurred while fetch properties cause by: " + error.message());
                            return Either.left(theError);
                        },
                        Either::right
                );
    }

    @Override
    public Either<ServiceError, Boolean> removePropertyTypeFromProperty(UUID userId, UUID propertyTypeId, UUID propertyId) {
        // find property type : get object
        // find property : get object
        // remove it
        return propertyTypeRepository.findPropertyTypeByIdAndUserId(propertyTypeId, userId)
                .mapLeft(propertyTypeError -> (ServiceError) new ServiceError.OperationFailed("Error while fetch property type by id, cause by: " + propertyTypeError.message()))
                .flatMapRight(foundedPropertyType -> {
                    return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                            .mapRight(foundedProperty -> Pair.of(foundedPropertyType, foundedProperty))
                            .mapLeft(propertyError -> new ServiceError.OperationFailed("Error while fetch property by id, cause by: " + propertyError.message()));
                }).flatMapRight(pair -> {
                   PropertyType propertyType = pair.getLeft();
                   Property property = pair.getRight();
                    if (property.getPropertyTypes().contains(propertyType)) {
                        property.removePropertyType(propertyType);
                        return Either.right(true);
                    }
                    return Either.right(false);
                });
    }

    @Override
    public Either<ServiceError, List<PropertyType>> assignPropertyTypeToProperty(UUID propertyId, List<UUID> propertyTypeIds, UUID userId) {
        // find property by id
        // clear all property type
        // fetch property type by id then assign each one to property
        // update property
        return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                .mapLeft(error -> (ServiceError) new ServiceError.OperationFailed("Error while fetching property by id, cause by: " + error.message()))
                .flatMapRight(property -> {
                    List<Either<RepositoryError, PropertyType>> foundPropertyTypes = propertyTypeIds.stream()
                            .map(id -> propertyTypeRepository.findPropertyTypeByIdAndUserId(id, userId))
                            .toList();

                    Optional<Either<RepositoryError, PropertyType>> firstError = foundPropertyTypes.stream()
                            .filter(Either::isLeft)
                            .findFirst();

                    if (firstError.isPresent()) {
                        RepositoryError repositoryError = firstError.get().getLeft();
                        ServiceError fetchPropertyTypeError = new ServiceError.BusinessRuleFailed("Error occurred while fetching property type, cause by: " + repositoryError.message());
                        return Either.left(fetchPropertyTypeError);
                    }

                    List<PropertyType> propertyTypeList = foundPropertyTypes.stream()
                            .map(Either::getRight)
                            .toList();

                    property.getPropertyTypes().clear();
                    property.getPropertyTypes().addAll(propertyTypeList);

                    return propertyRepository.updateProperty(property)
                            .mapRight(updatedProperty -> updatedProperty.getPropertyTypes().stream().toList())
                            .mapLeft(error -> new ServiceError.OperationFailed("Error occurred by updating property, cause by: " + error.message()));
                });
    }

    @Override
    public Either<ServiceError, List<PropertyType>> findAllPropertyTypesByPropertyId(UUID propertyId, UUID userId) {
        return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                .mapLeft(error -> (ServiceError) new ServiceError.OperationFailed("Error while fetch property by id, cause by: " + error.message()))
                .flatMapRight(property -> {
                    Set<PropertyType> propertyTypes = property.getPropertyTypes();
                    return Either.right(new ArrayList<>(propertyTypes));
                });
    }

    private Either<ServiceError, Boolean> helperDeletePropertyWithRelationFile(UUID propertyId, UUID userId) {
        // 1. Find the property and its associated files
        Either<RepositoryError, Property> isPropertyExist = propertyRepository.findPropertyByIdAndUserId(propertyId, userId);
        if(isPropertyExist.isLeft()) {
            ServiceError theError = new ServiceError.NotFound("Fail to delete Failed on fetch operation: " + isPropertyExist.getLeft().message());
            return Either.left(theError);
        }
        Property property = isPropertyExist.getRight();

        // 2. Get the associated FileDetail entities.
        Set<FileDetail> filesToDelete = property.getFileDetails();

        // 3. Delete the property (this will clear the join table entries for this property)
        Either<RepositoryError, Boolean> deletePropertyResult = propertyRepository.deletePropertyByIdAndUserId(property.getId(), userId);
        if(deletePropertyResult.isLeft()) {
            ServiceError theError = new ServiceError.OperationFailed("Error occurred while delete property cause by" + deletePropertyResult.getLeft().message() );
            return Either.left(theError);
        }
        if (!deletePropertyResult.getRight()) {
            ServiceError theError = new ServiceError.PersistenceFailed("Failed to delete property cause by" + deletePropertyResult.getLeft().message() );
            return Either.left(theError);
        }

        // 4. Check if a file is an orphan and delete it if so
        for (FileDetail file : filesToDelete) {
            Either<ServiceError, Boolean> deletedRelatedFileResult = fileDetailService.deleteFileDetailByFileIdAndUserId(file.getId(), userId);
            if (deletedRelatedFileResult.isLeft()) {
                ServiceError deleteRelationError = new ServiceError.OperationFailed("Error occurred by delete file associate with property cause by: " + deletedRelatedFileResult.getLeft().message());
                return Either.left(deleteRelationError);
            }
            if(!deletedRelatedFileResult.getRight()) {
                ServiceError deleteRelationError = new ServiceError.BusinessRuleFailed("Failed to delete file associate with property expect true to return but got : " + deletedRelatedFileResult.getRight());
                return Either.left(deleteRelationError);
            }
        }

        return Either.right(true);
    }

    @Override
    public Either<ServiceError, Boolean> assignMemoToProperty(UUID memoId, UUID propertyId, UUID userId) {
        // check is memo exist
        // check is property exist
        // assign memo to property
        return memoService.findMemoByIdAndUserId(memoId, userId)
                .mapLeft(memoError -> memoError)
                .flatMapRight(foundedMemo -> {
                   return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                           .mapRight(foundedProperty -> Pair.of(foundedMemo, foundedProperty))
                           .mapLeft(propertyError -> new ServiceError.OperationFailed("Failed to check is property exist:" + propertyError.message()));
                })
                .flatMapRight(pair -> {
                    Memo memo = pair.getLeft();
                    Property property = pair.getRight();
                    if(!property.getMemos().contains(memo)) {
                        property.addMemo(memo);
                        return Either.right(true);
                    }
                    return Either.right(false);
                });
    }

    @Override
    public Either<ServiceError, Boolean> removeMemoFromProperty(UUID memoId, UUID propertyId, UUID userId) {
        // check is memo exist
        // check is property exist
        // remove property from memo
        return memoService.findMemoByIdAndUserId(memoId, userId)
                .mapLeft(memoError -> (ServiceError) new ServiceError.OperationFailed("Failed to check is memo exist:" + memoError.message()))
                .flatMapRight(foundedMemo -> {
                    return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                            .mapRight(foundedProperty -> Pair.of(foundedMemo, foundedProperty))
                            .mapLeft(propertyError -> new ServiceError.OperationFailed("Failed to check is property exist:" + propertyError.message()));
                })
                .flatMapRight(pair -> {
                   Memo memo = pair.getLeft();
                   Property property = pair.getRight();
                   if(property.getMemos().contains(memo)) {
                       property.removeMemo(memo);
                       return Either.right(true);
                   }
                   return Either.right(false);
                });
    }

    @Override
    public Either<ServiceError, List<Memo>> findAllMemosByPropertyId(UUID propertyId, UUID userId) {
        // check is property exist
        // get all memo from property
        return propertyRepository.findPropertyByIdAndUserId(propertyId, userId)
                .flatMap(
                        error -> {
                            return Either.left(new ServiceError.OperationFailed("Error occurred while fetch property by id, cause by: " + error.message()));
                        },
                        targetFounded -> {
                            List<Memo> resultMemo = targetFounded.getMemos().stream().toList();
                            return Either.right(resultMemo);
                        }
                );
    }
}
