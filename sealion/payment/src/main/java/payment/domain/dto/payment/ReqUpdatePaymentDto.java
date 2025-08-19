package payment.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import payment.domain.dto.item.ReqCreatePaymentItemDto;
import payment.domain.dto.item.ReqUpdatePaymentItemDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePaymentDto {

    private String note;

    private String createdAt;

    private String contact;

    private String property;

    private List<ReqUpdatePaymentItemDto> items;

}
