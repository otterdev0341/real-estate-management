package contact.domain.mapper;

import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.Contact;
import common.domain.entity.ContactType;
import common.domain.entity.User;
import contact.domain.dto.contact.ResEntryContactDto;
import contact.domain.dto.contactType.ResEntryContactTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.List;

@Mapper(componentModel = "cdi")
public interface ContactMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "contactCreatedByUsername")
    @Mapping(source = "contactType", target = "contactType", qualifiedByName = "mapContactTypeDetail")
    ResEntryContactDto toDto(Contact contact);

    @Named("contactCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    @Named("mapContactTypeDetail")
    default String mapContactTypeDetail(ContactType contactType) {
        return contactType != null ? contactType.getDetail() : null;
    }

    List<ResEntryContactDto> toDtoList(List<Contact> contacts);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryContactDto> toResListBaseDto(String description, List<Contact> items);
}
