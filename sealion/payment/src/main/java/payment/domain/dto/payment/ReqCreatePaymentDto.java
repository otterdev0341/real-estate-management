package payment.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.dto.item.ReqCreatePaymentItemDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreatePaymentDto {

    private String note;

    private String createdAt;

    private String contact;

    private String property;

    private List<ReqCreatePaymentItemDto> items;


}
