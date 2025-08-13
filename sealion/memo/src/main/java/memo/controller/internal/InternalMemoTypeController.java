package memo.controller.internal;

import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import memo.domain.dto.memoType.ReqCreateMemoTypeDto;
import memo.domain.dto.memoType.ReqUpdateMemoTypeDto;
import java.util.UUID;



public interface InternalMemoTypeController {

    Response createMemoType(@Valid ReqCreateMemoTypeDto MemoTypeDto);

    Response updateMemoType(@Valid ReqUpdateMemoTypeDto MemoTypeDto, UUID MemoTypeId);

    Response deleteMemoType(UUID MemoTypeId);

    Response findMemoTypeById(UUID MemoTypeId);

    Response findAllMemoTypes(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );

}
