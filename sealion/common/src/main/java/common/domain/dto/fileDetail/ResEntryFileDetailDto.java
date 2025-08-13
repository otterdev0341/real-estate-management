package common.domain.dto.fileDetail;

import lombok.Data;

import java.util.UUID;

@Data
public class ResEntryFileDetailDto {

    private UUID id;

    private String fileName;

    private String urlPath;

    private String fileType;

    private String fileSize;

    private String createdBy;



}
