package payment.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.dto.item.ResEntryPaymentItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryPaymentDto {
    private UUID id;
    private String transaction;
    private String property;
    private String contact;
    private String note;
    private List<ResEntryPaymentItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
