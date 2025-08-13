package memo.domain.dto.memo;

import common.domain.dto.fileDetail.ResEntryFileDetailDto;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResEntryMemoDto {

    private UUID id;

    private String detail;

    private String memoType;

    private String createdBy;

    private List<ResEntryFileDetailDto> files;
}
