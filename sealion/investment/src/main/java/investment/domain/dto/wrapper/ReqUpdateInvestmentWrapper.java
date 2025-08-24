package investment.domain.dto.wrapper;

import investment.domain.dto.sub.ReqUpdateInvestmentDto;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqUpdateInvestmentWrapper {

    @FormParam("data")
    @PartType(MediaType.APPLICATION_JSON)
    private ReqUpdateInvestmentDto data;

}
