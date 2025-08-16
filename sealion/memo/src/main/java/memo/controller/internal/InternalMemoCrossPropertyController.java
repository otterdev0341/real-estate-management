package memo.controller.internal;

import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface InternalMemoCrossPropertyController {
    Response assignMemoToProperty(UUID memoId, UUID propertyId);

    Response removeMemoFromProperty(UUID memoId, UUID propertyId);

    Response findAllMemosByPropertyId(UUID propertyId);
}
