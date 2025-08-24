package investment.domain.dto.sub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreateInvestmentDto {

    private String note;

    private LocalDateTime createdAt;

    private UUID property;

    private List<InvestmentItemDto> items;


}
