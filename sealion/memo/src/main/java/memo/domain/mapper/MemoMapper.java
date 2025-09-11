package memo.domain.mapper;


import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.*;
import common.domain.mapper.FileDetailMapper;
import memo.domain.dto.memo.ReqCreateMemoDto;
import memo.domain.dto.memo.ReqUpdateMemoDto;
import memo.domain.dto.memo.ResEntryMemoDto;
import memo.domain.dto.memo.form.ReqCreateMemoForm;
import memo.domain.dto.memo.form.ReqUpdateMemoForm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(
        componentModel = "cdi",
        uses = { FileDetailMapper.class } // <-- inject here
)
public interface MemoMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    @Mapping(source = "memoType", target = "memoType", qualifiedByName = "mapMemoTypeDetail")
    @Mapping(source = "fileDetails", target = "files") // will use FileDetailMapper
    ResEntryMemoDto toDto(Memo memo);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    @Named("mapMemoTypeDetail")
    default String mapMemoTypeDetail(MemoType memoType) {
        return memoType != null ? memoType.getDetail() : null;
    }

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryMemoDto> toResListBaseDto(String description, List<Memo> items);

    // Helper for list mapping
    List<ResEntryMemoDto> toDtoList(List<Memo> memos);

    @Mapping(target = "memoDate", expression = "java(formDto.getMemoDate() != null ? java.time.LocalDateTime.parse(formDto.getMemoDate()) : null)")
    ReqCreateMemoDto tryFormToDto(ReqCreateMemoForm formDto);

    @Mapping(target = "memoDate", expression = "java(formDto.getMemoDate() != null ? java.time.LocalDateTime.parse(formDto.getMemoDate()) : null)")
    ReqUpdateMemoDto tryFormToDto(ReqUpdateMemoForm formDto);

}
