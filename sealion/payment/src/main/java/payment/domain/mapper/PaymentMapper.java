package payment.domain.mapper;

import common.domain.entity.Contact;
import common.domain.entity.Property;
import common.domain.entity.Transaction;
import common.domain.entity.payment.PaymentTransaction;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import payment.domain.dto.payment.ResEntryPaymentDto;

@Mapper(
        componentModel = "cdi",
        uses = {PaymentItemMapper.class} // Add this line
)
public interface PaymentMapper {

    @Mapping(source = "transaction", target = "transaction", qualifiedByName = "mapPaymentTransaction")
    @Mapping(source = "property", target = "property", qualifiedByName = "mapPaymentProperty")
    @Mapping(source = "contact", target = "contact", qualifiedByName = "mapPaymentContact")
    @Mapping(source = "transaction", target = "note", qualifiedByName = "mapPaymentNote")
    @Mapping(source = "expenseItems", target = "items")
    ResEntryPaymentDto toDto(PaymentTransaction paymentTransaction);

    @Named("mapPaymentTransaction")
    default String mapPaymentTransaction(Transaction transaction) {
        return transaction != null ? transaction.getTransactionType().getDetail() : null;
    }

    @Named("mapPaymentProperty")
    default String mapPaymentProperty(Property property) {
        return property != null ? property.getName() : null;
    }

    @Named("mapPaymentContact")
    default String mapPaymentContact(Contact contact) {
        return contact != null ? contact.getBusinessName() : null;
    }

    @Named("mapPaymentNote")
    default String mapPaymentNote(Transaction transaction) {
        return transaction != null ? transaction.getNote() : null;
    }

    default List<ResEntryPaymentDto> toDtoList(List<PaymentTransaction> paymentTransactionList) {
        return paymentTransactionList.stream().map(this::toDto).collect(Collectors.toList());
    }

}
