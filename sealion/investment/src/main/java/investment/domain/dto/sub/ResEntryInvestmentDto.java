package investment.domain.dto.sub;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryInvestmentDto {
    private UUID id;
    private String note;
    private UUID transaction;
    private String property;
    private List<ResEntryInvestmentItemDto> items;
    private LocalDateTime investmentDate;
    private LocalDateTime updatedAt;
}
