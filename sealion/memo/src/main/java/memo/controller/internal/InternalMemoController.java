package memo.controller.internal;

import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import memo.domain.dto.memo.form.ReqCreateMemoForm;
import memo.domain.dto.memo.form.ReqUpdateMemoForm;

import java.util.UUID;

public interface InternalMemoController {

    Response createMemo(@Valid ReqCreateMemoForm reqCreateMemoForm);

    Response updateMemo(@Valid ReqUpdateMemoForm reqUpdateMemoForm, UUID memoId);

    Response deleteMemo(UUID memoId);

    Response findMemoById(UUID memoId);

    Response findAllMemos(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );
}
