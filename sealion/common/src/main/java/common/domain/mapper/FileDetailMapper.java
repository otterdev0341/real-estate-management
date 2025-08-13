package common.domain.mapper;

import common.domain.dto.fileDetail.ResEntryFileDetailDto;
import common.domain.entity.FileDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "cdi")
public interface FileDetailMapper {

    @Mapping(source = "name", target = "fileName")
    @Mapping(source = "path", target = "urlPath")
    @Mapping(source = "type.detail", target = "fileType")
    @Mapping(source = "size", target = "fileSize", qualifiedByName = "formatFileSize")
    @Mapping(source = "createdBy.username", target = "createdBy")
    ResEntryFileDetailDto toDto(FileDetail fileDetail);

    // Example: format size as KB/MB
    @Named("formatFileSize")
    default String formatFileSize(Long size) {
        if (size == null) return null;
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return (size / 1024) + " KB";
        return (size / (1024 * 1024)) + " MB";
    }
}
