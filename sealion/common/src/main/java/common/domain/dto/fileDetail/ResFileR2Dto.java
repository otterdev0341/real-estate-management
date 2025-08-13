package common.domain.dto.fileDetail;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResFileR2Dto {
    private String objectKey;
    private String fileUrl;
    private String fileName;
    private String contentType;
    private Long contentLength;
}
