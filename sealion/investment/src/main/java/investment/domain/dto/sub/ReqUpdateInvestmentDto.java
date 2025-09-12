package investment.domain.dto.sub;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
            if (this.investmentDate == null || this.investmentDate.isBlank()) {
                return null;
            }
            // Parse the string with timezone info (Z for UTC) into a ZonedDateTime
            // and then convert it to a LocalDateTime.
            return ZonedDateTime.parse(this.investmentDate).toLocalDateTime();
        } catch (DateTimeParseException e) {
            return null;
        }

    }

}
