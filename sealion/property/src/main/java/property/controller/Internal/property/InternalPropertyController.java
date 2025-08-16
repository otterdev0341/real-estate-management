package property.controller.Internal.property;

import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import property.domain.dto.property.ReqCreatePropertyDto;
import property.domain.dto.property.ReqUpdatePropertyDto;
import property.domain.dto.property.form.ReqCreatePropertyForm;
import property.domain.dto.property.form.ReqUpdatePropertyForm;

import java.util.UUID;

public interface InternalPropertyController {

    Response createProperty(@Valid ReqCreatePropertyForm reqCreatePropertyForm);

    Response updateProperty(@Valid ReqUpdatePropertyForm reqUpdatePropertyForm, UUID propertyId);

    Response deleteProperty(UUID propertyId);

    Response findPropertyById(UUID propertyId);

    Response findAllProperties(
            @Parameter(description = "to use with pagination", example = "") @QueryParam("page") Integer page,
            @Parameter(description = "to use with pagination", example = "") @QueryParam("size") Integer size,
            @Parameter(description = "sort by createdAt, detail", example = "") @QueryParam("sortBy") String sortBy,
            @Parameter(description = "ASC | DESC (default: DESC)", example = "") @QueryParam("sortDirection") String sortDirection
    );

}
