package sale.controller.internal;

import common.controller.declare.FileAssetManagementController;
import common.domain.dto.query.BaseQuery;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import sale.domain.dto.form.ReqCreateSaleForm;
import sale.domain.dto.form.ReqUpdateSaleForm;

import java.util.UUID;

public interface InternalSaleTransactionController extends FileAssetManagementController {

    Response createNewSaleTransaction(@BeanParam @Valid ReqCreateSaleForm reqCreateSaleForm);

    Response updateNewSaleTransaction(@BeanParam @Valid ReqUpdateSaleForm reqUpdateSaleForm, UUID saleTransactionId);

    Response findSaleTransactionById(@RequestBody(required = false) UUID saleTransactionId);

    Response deleteSaleTransaction(@RequestBody(required = false) UUID saleTransactionId);

    Response findAllSaleTransaction(
            @RequestBody(required = false)
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDirection") String sortDirection
    );

}
