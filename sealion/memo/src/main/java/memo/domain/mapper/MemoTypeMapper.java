package memo.domain.mapper;


import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.ContactType;
import common.domain.entity.MemoType;
import common.domain.entity.User;
import memo.domain.dto.memoType.ResEntryMemoTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface MemoTypeMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    ResEntryMemoTypeDto toDto(MemoType memoType);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    List<ResEntryMemoTypeDto> toDtoList(List<MemoType> memoTypes);


    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryMemoTypeDto> toResListBaseDto(String description, List<MemoType> items);



}
