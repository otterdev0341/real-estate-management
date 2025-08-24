package investment.domain.dto.wrapper;

import investment.domain.dto.sub.ReqCreateInvestmentDto;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreateInvestmentWrapperForm {

    @FormParam("data")
    @PartType(MediaType.APPLICATION_JSON)
    private ReqCreateInvestmentDto data;

    @FormParam("files")
    @PartType(MediaType.MULTIPART_FORM_DATA)
    private List<FileUpload> files;

}
