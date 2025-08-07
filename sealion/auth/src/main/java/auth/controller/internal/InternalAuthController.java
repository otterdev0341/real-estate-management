package auth.controller.internal;

import common.domain.dto.auth.ReqLoginDto;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;

public interface InternalAuthController {

    Response login(@Valid ReqLoginDto reqLoginDto);

    Response resMe();
    
}
