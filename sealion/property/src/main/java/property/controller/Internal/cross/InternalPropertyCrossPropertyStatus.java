package property.controller.Internal.cross;

import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import property.domain.dto.propertyType.ReqAssignPropertyType;

import java.util.UUID;

public interface InternalPropertyCrossPropertyStatus {
    // update assign property type to property
    Response assignPropertyTypeToProperty(UUID propertyId, @Valid ReqAssignPropertyType reqAssignPropertyType);

    Response findAllPropertyTypesByPropertyId(UUID propertyId);

    Response removePropertyTypeFromProperty(UUID propertyTypeId, UUID propertyId);
}
