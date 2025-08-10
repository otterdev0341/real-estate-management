package contact.domain.mapper;


import java.util.List;

import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.ContactType;
import common.domain.entity.User;
import contact.domain.dto.contactType.ResEntryContactTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;





@Mapper(componentModel = "cdi")
public interface ContactTypeMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    ResEntryContactTypeDto toDto(ContactType contactType);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    List<ResEntryContactTypeDto> toDtoList(List<ContactType> contactTypes);


    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryContactTypeDto> toResListBaseDto(String description, List<ContactType> items);
}
