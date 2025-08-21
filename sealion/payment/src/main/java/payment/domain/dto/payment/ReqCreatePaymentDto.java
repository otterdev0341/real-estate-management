package payment.domain.dto.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.dto.item.ReqCreatePaymentItemDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreatePaymentDto {

    @NotBlank(message = "Payment name is required")
    private String note;

    private LocalDateTime createdAt;

    private UUID contact;

    private UUID property;

    private List<ReqCreatePaymentItemDto> items;


}
