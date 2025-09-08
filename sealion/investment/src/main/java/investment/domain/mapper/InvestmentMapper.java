package investment.domain.mapper;

import common.domain.entity.Property;
import common.domain.entity.investment.InvestmentTransaction;
import investment.domain.dto.sub.ResEntryInvestmentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(
        componentModel = "cdi",
        uses = {InvestmentItemMapper.class}
)
public interface InvestmentMapper {

    @Mapping(source = "transaction.note", target = "note")
    @Mapping(source = "transaction.id", target = "transaction")
    @Mapping(source = "property", target = "property", qualifiedByName = "mapInvestmentProperty")
    @Mapping(source = "investmentItems", target = "items")
    ResEntryInvestmentDto toDto(InvestmentTransaction investmentTransaction);

    @Named("mapInvestmentProperty")
    default String mapInvestmentProperty(Property property) {
        return property != null ? property.getName() : null;
    }

    default List<ResEntryInvestmentDto> toDtoList(List<InvestmentTransaction> investmentTransactions) {
        return investmentTransactions.stream().map(this::toDto).collect(Collectors.toList());
    }
}
