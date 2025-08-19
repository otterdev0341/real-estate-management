package sale.domain.mapper;

import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import sale.domain.dto.ReqCreateSaleDto;
import sale.domain.dto.ReqUpdateSaleDto;
import sale.domain.dto.ResEntrySaleDto;
import sale.domain.dto.form.ReqCreateSaleForm;
import sale.domain.dto.form.ReqUpdateSaleForm;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface SaleMapper {

    @Mapping(source = "transaction.transactionType", target = "transactionType", qualifiedByName = "mapTransactionTypeDetail")
    @Mapping(source = "contact", target = "contact", qualifiedByName = "mapContactBusinessDetail")
    @Mapping(source = "property", target = "property", qualifiedByName = "mapProperty")
    @Mapping(source = "transaction.createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    ResEntrySaleDto toDto(SaleTransaction saleTransaction);

    List<ResEntrySaleDto> toDtoList(List<SaleTransaction> saleTransactions);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntrySaleDto> toResListBaseDto(String description, List<SaleTransaction> items);

    // helper methods
    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User user) {
        return user != null ? user.getUsername() : null;
    }

    @Named("mapContactBusinessDetail")
    default String mapContactBusinessDetail(Contact contact) {
        return contact != null ? contact.getBusinessName() : null;
    }

    @Named("mapTransactionTypeDetail")
    default String mapTransactionTypeDetail(TransactionType transactionType) {
        return transactionType != null ? transactionType.getDetail() : null;
    }

    @Named("mapProperty")
    default String mapProperty(Property property) {
        return property != null ? property.getName() : null;
    }

    // form mapper
    ReqCreateSaleDto tryFormToDto(ReqCreateSaleForm formDto);

    ReqUpdateSaleDto tryFormToDto(ReqUpdateSaleForm formDto);

}