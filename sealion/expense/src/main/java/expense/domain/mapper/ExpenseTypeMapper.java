package expense.domain.mapper;

import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.ExpenseType;
import common.domain.entity.User;
import expense.domain.dto.expenseType.ResEntryExpenseTypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")


public interface ExpenseTypeMapper {

    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    ResEntryExpenseTypeDto toDto(ExpenseType expenseType);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    List<ResEntryExpenseTypeDto> toDtoList(List<ExpenseType> expenseTypes);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryExpenseTypeDto> toResListBaseDto(String description, List<ExpenseType> items);


}
