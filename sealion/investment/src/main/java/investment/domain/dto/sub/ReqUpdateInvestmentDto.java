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
public class ReqUpdateInvestmentDto {

    private String note;

    private LocalDateTime createdAt;

    private UUID property;

    private List<InvestmentItemDto> items;

}
