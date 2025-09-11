package memo.domain.dto.memo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqUpdateMemoDto {

    private LocalDateTime memoDate;

    private String name;

    private String detail;

    private UUID memoType;

}
