package payment.domain.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreatePaymentItemDto {

    private String id;

    private String expense;

    private String amount;

    private String price;

}
