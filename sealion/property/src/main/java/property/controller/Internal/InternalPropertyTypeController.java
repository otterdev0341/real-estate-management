package property.controller.Internal;

import contact.domain.dto.contactType.ReqCreateContactTypeDto;
import contact.domain.dto.contactType.ReqUpdateContactTypeDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import property.domain.dto.propertyType.ReqCreatePropertyTypeDto;
import property.domain.dto.propertyType.ReqUpdatePropertyTypeDto;

import java.util.UUID;

public interface InternalPropertyTypeController {

    Response createPropertyType(@Valid ReqCreatePropertyTypeDto reqCreatePropertyTypeDto);

    Response updatePropertyType(@Valid ReqUpdatePropertyTypeDto reqUpdatePropertyTypeDto, UUID propertyTypeId);

    Response deletePropertyType(UUID propertyTypeId);

    Response findPropertyTypeById(UUID propertyTypeId);

    Response findAllPropertyTypes(
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    );

}
