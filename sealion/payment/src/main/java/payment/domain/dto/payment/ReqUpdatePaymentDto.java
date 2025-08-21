package payment.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.dto.item.ReqCreatePaymentItemDto;
import payment.domain.dto.item.ReqUpdatePaymentItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePaymentDto {

    private String note;

    private LocalDateTime createdAt;

    private UUID contact;

    private UUID property;

    private List<ReqUpdatePaymentItemDto> items;

}
