package memo.domain.dto.memo;

import common.domain.dto.fileDetail.ResEntryFileDetailDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ResEntryMemoDto {

    private UUID id;

    private String name;

    private String detail;

    private String memoType;

    private String createdBy;

    private List<ResEntryFileDetailDto> files;

    private LocalDateTime memoDate;

    private LocalDateTime updatedAt;
}
