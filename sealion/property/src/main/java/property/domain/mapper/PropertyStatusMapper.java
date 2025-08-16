package property.domain.mapper;


import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.PropertyStatus;
import common.domain.entity.PropertyType;
import common.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import property.domain.dto.propertyStatus.ResEntryPropertyStatusDto;
import property.domain.dto.propertyType.ResEntryPropertyTypeDto;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PropertyStatusMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    ResEntryPropertyStatusDto toDto(PropertyStatus propertyStatus);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    List<ResEntryPropertyStatusDto> toDtoList(List<PropertyStatus> propertyStatuses);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryPropertyStatusDto> toResListBaseDto(String description, List<PropertyStatus> items);

}
