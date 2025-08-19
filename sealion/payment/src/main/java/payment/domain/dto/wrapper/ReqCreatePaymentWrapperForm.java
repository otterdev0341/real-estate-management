package payment.domain.dto.wrapper;


import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import payment.domain.dto.payment.ReqCreatePaymentDto;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqCreatePaymentWrapperForm {

    /*เพิ่ม Item ใหม่ → ต้อง insert เข้า DB

    ลบ Item เดิม → ต้อง delete ออกจาก DB

    แก้ไข Item เดิม → ต้อง update row เดิม

     */

    @FormParam("data")
    @PartType(MediaType.APPLICATION_JSON)
    private ReqCreatePaymentDto data;

    @FormParam("files")
    @PartType(MediaType.MULTIPART_FORM_DATA)
    private List<FileUpload> files;
}
