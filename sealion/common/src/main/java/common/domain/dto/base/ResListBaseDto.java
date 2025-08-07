package common.domain.dto.base;

import java.util.List;

import lombok.Data;

@Data
public class ResListBaseDto<T> {
    private String description;
    private int totalCount;
    private List<T> items;

    // Getters and Setters
}
