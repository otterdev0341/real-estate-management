package payment.controller.internal;

import common.controller.declare.FileAssetManagementController;
import jakarta.validation.Valid;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import payment.domain.dto.wrapper.ReqCreatePaymentWrapperForm;
import payment.domain.dto.wrapper.ReqUpdatePaymentWrapperForm;

import java.util.UUID;

public interface InternalPaymentController extends FileAssetManagementController {

    Response createNewPayment(@Valid ReqCreatePaymentWrapperForm reqCreatePaymentWrapperForm);

    Response findPaymentById(UUID paymentId);

    Response updatePayment(@Valid ReqUpdatePaymentWrapperForm reqUpdatePaymentWrapperForm, UUID paymentId);

    Response deletePayment(UUID paymentId);

    Response getAllPaymentByPropertyId(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );

}
