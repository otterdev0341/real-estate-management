package expense.domain.mapper;


import common.domain.dto.base.ResListBaseDto;
import common.domain.entity.*;
import expense.domain.dto.expense.ResEntryExpenseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ExpenseMapper {

    @Mapping(source = "detail", target = "expense")
    @Mapping(source = "createdBy", target = "createdBy", qualifiedByName = "mapCreatedByUsername")
    @Mapping(source = "expenseType", target = "expenseType", qualifiedByName = "mapExpenseTypeDetail")
    ResEntryExpenseDto toDto(Expense expense);

    @Named("mapCreatedByUsername")
    default String mapCreatedByUsername(User createdBy) {
        return createdBy != null ? createdBy.getUsername() : null;
    }

    @Named("mapExpenseTypeDetail")
    default String mapExpenseTypeDetail(ExpenseType expenseType) {
        return expenseType != null ? expenseType.getDetail() : null;
    }

    List<ResEntryExpenseDto> toDtoList(List<Expense> expenses);

    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalCount", expression = "java(items != null ? items.size() : 0)")
    @Mapping(target = "items", expression = "java(toDtoList(items))")
    ResListBaseDto<ResEntryExpenseDto> toResListBaseDto(String description, List<Expense> items);

}
