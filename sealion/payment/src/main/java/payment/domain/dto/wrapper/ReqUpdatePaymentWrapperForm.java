package payment.domain.dto.wrapper;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import payment.domain.dto.payment.ReqUpdatePaymentDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdatePaymentWrapperForm {
    @FormParam("data")
    @PartType(MediaType.APPLICATION_JSON)
    private ReqUpdatePaymentDto data;
}
