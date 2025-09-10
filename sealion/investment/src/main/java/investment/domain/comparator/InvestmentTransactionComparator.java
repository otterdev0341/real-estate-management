package investment.domain.comparator;

import common.domain.entity.investment.InvestmentTransaction;
import common.domain.entity.payment.PaymentTransaction;
import jakarta.inject.Singleton;

import java.util.Comparator;

@Singleton
public class InvestmentTransactionComparator {

    public static final Comparator<InvestmentTransaction> BY_CREATED_AT =
            Comparator.comparing(investmentTransaction -> investmentTransaction.getTransaction().getCreatedAt());

    public static final Comparator<InvestmentTransaction> BY_PROPERTY_ID =
            Comparator.comparing(investmentTransaction -> investmentTransaction.getProperty().getId());
}
