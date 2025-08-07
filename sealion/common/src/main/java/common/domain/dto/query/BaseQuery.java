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
    @QueryParam("page")
    @Builder.Default
    private Integer page = 0;

    @QueryParam("size")
    @Builder.Default
    private Integer size = 10;

    @QueryParam("sortBy")
    private String sortBy;

    @QueryParam("sortDirection")
    private String sortDirection;


}
