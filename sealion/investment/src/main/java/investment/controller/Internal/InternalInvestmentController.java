package investment.controller.Internal;

import common.controller.declare.FileAssetManagementController;
import investment.domain.dto.wrapper.ReqCreateInvestmentWrapperForm;
import investment.domain.dto.wrapper.ReqUpdateInvestmentWrapper;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface InternalInvestmentController  extends FileAssetManagementController {

    Response createNewInvestment(@Valid ReqCreateInvestmentWrapperForm reqCreateInvestmentWrapperForm);

    Response findInvestmentById(UUID investmentId);

    Response updateInvestment(@Valid ReqUpdateInvestmentWrapper reqUpdateInvestmentWrapper, UUID investmentId);

    Response deleteInvestment(UUID investmentId);

    Response getAllInvestment(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );

}
