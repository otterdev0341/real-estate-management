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
public class ResEntrySaleDto {

    private UUID id;

    private String transactionType;

    private String property;

    private String contact;

    private BigDecimal price;

    private String createdBy;

    private LocalDateTime saleDate;

    private LocalDateTime updatedAt;

}
