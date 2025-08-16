package property.domain.mapper;


import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.PropertyType;
import common.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import property.domain.dto.propertyType.ResEntryPropertyTypeDto;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PropertyTypeMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    ResEntryPropertyTypeDto toDto(PropertyType propertyType);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    List<ResEntryPropertyTypeDto> toDtoList(List<PropertyType> propertyType);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryPropertyTypeDto> toResListBaseDto(String description, List<PropertyType> items);

}
