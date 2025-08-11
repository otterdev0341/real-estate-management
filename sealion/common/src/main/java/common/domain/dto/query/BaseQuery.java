package common.domain.dto.query;

import jakarta.ws.rs.QueryParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;



@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseQuery {

    @Builder.Default
    private Integer page = 0;


    @Builder.Default
    private Integer size = 10;


    private String sortBy;


    private String sortDirection;


}
