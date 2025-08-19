package sale.domain.comparator;

import common.domain.entity.ContactType;
import common.domain.entity.SaleTransaction;
import jakarta.inject.Singleton;

import java.util.Comparator;

@Singleton
public class SaleTransactionComparators {

    public static final Comparator<SaleTransaction> BY_CREATED_AT =
            Comparator.comparing(SaleTransaction::getCreatedAt);


}
