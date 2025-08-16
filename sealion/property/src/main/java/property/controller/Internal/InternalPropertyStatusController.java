package property.controller.Internal;

import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import property.domain.dto.propertyStatus.ReqCreatePropertyStatusDto;
import property.domain.dto.propertyStatus.ReqUpdatePropertyStatusDto;
import property.domain.dto.propertyType.ReqCreatePropertyTypeDto;
import property.domain.dto.propertyType.ReqUpdatePropertyTypeDto;

import java.util.UUID;

public interface InternalPropertyStatusController {

    Response createPropertyStatus(@Valid ReqCreatePropertyStatusDto reqCreatePropertyStatusDto);

    Response updatePropertyStatus(@Valid ReqUpdatePropertyStatusDto reqUpdatePropertyStatusDto, UUID propertyStatusId);

    Response deletePropertyStatus(UUID propertyStatusId);

    Response findPropertyStatusById(UUID propertyStatusId);

    Response findAllPropertyStatuses(
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    );

}
