package investment.domain.dto.sub;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateInvestmentDto {

    private String note;

    private String investmentDate;

    private UUID property;

    private List<InvestmentItemDto> items;

    public LocalDateTime getPersistInvestmentDate() {
        try {
            return LocalDateTime.parse(this.investmentDate);
        } catch (DateTimeParseException e) {
            return null;
        }

    }

}
