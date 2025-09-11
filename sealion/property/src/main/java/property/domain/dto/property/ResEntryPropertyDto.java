package property.domain.dto.property;


import common.domain.dto.fileDetail.ResEntryFileDetailDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import memo.domain.dto.memo.ResEntryMemoDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResEntryPropertyDto {

    private UUID id;

    private String name;

    private String description;

    private String specific;

    private String highlight;

    private String area;

    private BigDecimal price;

    private BigDecimal fsp;

    private BigDecimal budget;

    private String propertyStatus;

    private String ownerBy;

    private String mapUrl;

    private String lat;

    private String lng;

    private Boolean sold;

    private String createdBy;

    private List<String> propertyTypes;

    private List<ResEntryFileDetailDto> files;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
