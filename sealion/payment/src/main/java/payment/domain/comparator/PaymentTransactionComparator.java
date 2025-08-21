package payment.domain.comparator;

import common.domain.entity.ContactType;
import common.domain.entity.payment.PaymentTransaction;
import jakarta.inject.Singleton;

import java.util.Comparator;

@Singleton
public class PaymentTransactionComparator {

    public static final Comparator<PaymentTransaction> BY_CREATED_AT =
            Comparator.comparing(paymentTransaction -> paymentTransaction.getTransaction().getCreatedAt());

}
