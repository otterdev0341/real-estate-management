package property.domain.mapper;

import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.*;
import common.domain.mapper.FileDetailMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import property.domain.dto.property.ReqCreatePropertyDto;
import property.domain.dto.property.ReqUpdatePropertyDto;
import property.domain.dto.property.ResEntryPropertyDto;
import property.domain.dto.property.form.ReqCreatePropertyForm;
import property.domain.dto.property.form.ReqUpdatePropertyForm;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper(
        componentModel = "cdi",
        uses = { FileDetailMapper.class } // <-- inject here
)
public interface PropertyMapper {

    @Mapping(source = "ownerBy", target = "ownerBy", qualifiedByName = "MapOwnerBusinessName")
    @Mapping(source = "propertyTypes", target = "propertyTypes", qualifiedByName = "MapPropertyTypeDetail")
    @Mapping(source = "status", target = "propertyStatus", qualifiedByName = "MapPropertyStatusDetail")
    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    @Mapping(source = "fileDetails", target = "files") // will use FileDetailMapper
    ResEntryPropertyDto toDto(Property property);

    // helper

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    @Named("MapPropertyStatusDetail")
    default String mapPropertyStatusDetail(PropertyStatus propertyStatus) {
        return propertyStatus != null ? propertyStatus.getDetail() : null;
    }

    @Named("MapOwnerBusinessName")
    default String mapOwner(Contact contact) {
        return contact != null ? contact.getBusinessName() : null;
    }



    @Named("MapPropertyTypeDetail")
    default List<String> mapPropertyType(Set<PropertyType> propertyTypes) {
        return propertyTypes != null ? propertyTypes.stream().map(PropertyType::getDetail).toList() : null;
    }

    // list converter

    List<ResEntryPropertyDto> toDtoList(List<Property> propertyStatuses);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryPropertyDto> toResListBaseDto(String description, List<Property> items);

    // form converter


    ReqCreatePropertyDto tryFormToDto(ReqCreatePropertyForm formDto);

    ReqUpdatePropertyDto tryFormToDto(ReqUpdatePropertyForm formDto);

    @Named("convertStringFormToUUID")
    default UUID convertStringFormToUUID(String targetData) {
        return targetData != null ? UUID.fromString(targetData) : null;
    }

}
