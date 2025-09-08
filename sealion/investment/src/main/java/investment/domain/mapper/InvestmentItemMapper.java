package investment.domain.mapper;

import common.domain.entity.Contact;
import common.domain.entity.investment.InvestmentItem;
import investment.domain.dto.sub.ResEntryInvestmentItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface InvestmentItemMapper {

    @Mapping(source = "investment.id", target = "investTransaction")
    @Mapping(source = "contact", target = "contact", qualifiedByName = "mapInvestmentItemContact")
    ResEntryInvestmentItemDto toDto(InvestmentItem investmentItem);

    @Named("mapInvestmentItemContact")
    default String mapInvestmentItemContact(Contact contact) {
        return contact != null ? contact.getBusinessName() : null;
    }

    List<ResEntryInvestmentItemDto> toDtoList(List<InvestmentItem> investmentItems);
}
