package payment.domain.mapper;

import common.domain.entity.Expense;
import common.domain.entity.payment.PaymentItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import payment.domain.dto.item.ResEntryPaymentItemDto;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface PaymentItemMapper {

    @Mapping(source = "payment.id", target = "paymentTransaction")
    @Mapping(source = "expense", target = "expense", qualifiedByName = "mapPaymentItemExpense")
    ResEntryPaymentItemDto toDto(PaymentItem paymentItem);

    @Named("mapPaymentItemExpense")
    default String mapPaymentItemExpense(Expense expense) {
        return expense != null ? expense.getDetail() : null;
    }

    List<ResEntryPaymentItemDto> toDtoList(List<PaymentItem> paymentItemList);
}
