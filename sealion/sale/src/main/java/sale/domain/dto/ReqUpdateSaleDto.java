package sale.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateSaleDto {

    private LocalDateTime saleDate;

    private String note;

    private UUID propertyId;

    private UUID contactId;

    private BigDecimal price;
}
