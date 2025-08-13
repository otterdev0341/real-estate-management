package expense.domain.comparator;


import common.domain.entity.Expense;
import jakarta.inject.Singleton;

import java.util.Comparator;

@Singleton
public class ExpenseComparators {
    // Comparator for businessName (case-insensitive)
    public static final Comparator<Expense> BY_DETAIL =
            Comparator.comparing(Expense::getDetail, String.CASE_INSENSITIVE_ORDER);

    // Comparator for createdAt
    public static final Comparator<Expense> BY_CREATED_AT =
            Comparator.comparing(Expense::getCreatedAt);

    // Comparator for expenseType.detail (case-insensitive)
    public static final Comparator<Expense> BY_EXPENSE_TYPE_DETAIL =
            Comparator.comparing(expense -> expense.getExpenseType().getDetail(), String.CASE_INSENSITIVE_ORDER);


}
