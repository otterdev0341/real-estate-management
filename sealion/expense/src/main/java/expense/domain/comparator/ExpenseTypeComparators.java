package expense.domain.comparator;

import common.domain.entity.ContactType;
import common.domain.entity.ExpenseType;
import jakarta.inject.Singleton;

import java.util.Comparator;

@Singleton
public class ExpenseTypeComparators {

    public static final Comparator<ExpenseType> BY_DETAIL =
        Comparator.comparing(ExpenseType::getDetail, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<ExpenseType> BY_CREATED_AT =
        Comparator.comparing(ExpenseType::getCreatedAt);

    public static final Comparator<ExpenseType> BY_DETAIL_THEN_CREATED_AT =
        BY_DETAIL.thenComparing(BY_CREATED_AT);
}
