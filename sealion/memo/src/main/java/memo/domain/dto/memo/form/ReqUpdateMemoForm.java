package memo.domain.dto.memo.form;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.core.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReqUpdateMemoForm {

    @RestForm("name")
    @NotBlank(message = "name of memo is required")
    @PartType(MediaType.TEXT_PLAIN)
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @RestForm("detail")
    @PartType(MediaType.TEXT_PLAIN)
    private String detail;

    @RestForm("memoType")
    @PartType(MediaType.TEXT_PLAIN)
    @NotNull(message = "Memo type is required")
    private UUID memoType;

}
