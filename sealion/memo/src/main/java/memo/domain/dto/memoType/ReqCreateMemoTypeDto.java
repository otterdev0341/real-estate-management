package memo.domain.dto.memoType;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReqCreateMemoTypeDto {
    @NotBlank(message = "Detail must not be empty")
    private String detail;
}
