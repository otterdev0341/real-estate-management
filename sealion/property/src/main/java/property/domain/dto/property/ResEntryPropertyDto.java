package property.domain.dto.property;


import common.domain.dto.fileDetail.ResEntryFileDetailDto;
import lombok.Data;
import memo.domain.dto.memo.ResEntryMemoDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ResEntryPropertyDto {

    private UUID ud;

    private String name;

    private String description;

    private String specific;

    private String highlight;

    private String area;

    private BigDecimal price;

    private BigDecimal fsp;


    private String propertyStatus;


    private String ownerBy;

    private String mapUrl;

    private String lat;

    private String lng;

    private Boolean sold;

    private String createdBy;

    private List<String> propertyType;

    private List<ResEntryFileDetailDto> files;

    private List<ResEntryMemoDto> memos;

}
