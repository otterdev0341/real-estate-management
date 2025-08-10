package contact.domain.comparator;

import java.util.Comparator;

import common.domain.entity.ContactType;
import jakarta.inject.Singleton;

@Singleton
public class ContactTypeComparators {

    public static final Comparator<ContactType> BY_DETAIL =
        Comparator.comparing(ContactType::getDetail, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<ContactType> BY_CREATED_AT =
        Comparator.comparing(ContactType::getCreatedAt);

    public static final Comparator<ContactType> BY_DETAIL_THEN_CREATED_AT =
        BY_DETAIL.thenComparing(BY_CREATED_AT);
}
