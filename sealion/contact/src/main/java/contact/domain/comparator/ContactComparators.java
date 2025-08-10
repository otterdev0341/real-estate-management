package contact.domain.comparator;

import java.util.Comparator;


import common.domain.entity.Contact;
import jakarta.inject.Singleton;

@Singleton
public class ContactComparators {

    // Comparator for businessName (case-insensitive)
    public static final Comparator<Contact> BY_BUSINESS_NAME =
        Comparator.comparing(Contact::getBusinessName, String.CASE_INSENSITIVE_ORDER);

    // Comparator for createdAt
    public static final Comparator<Contact> BY_CREATED_AT =
        Comparator.comparing(Contact::getCreatedAt);

    // Comparator for contactType.detail (case-insensitive)
    public static final Comparator<Contact> BY_CONTACT_TYPE_DETAIL =
        Comparator.comparing(contact -> contact.getContactType().getDetail(), String.CASE_INSENSITIVE_ORDER);

    // Combined comparator: businessName, then createdAt
    public static final Comparator<Contact> BY_BUSINESS_NAME_THEN_CREATED_AT =
        BY_BUSINESS_NAME.thenComparing(BY_CREATED_AT);

    // Combined comparator: contactType.detail, then createdAt
    public static final Comparator<Contact> BY_CONTACT_TYPE_DETAIL_THEN_CREATED_AT =
        BY_CONTACT_TYPE_DETAIL.thenComparing(BY_CREATED_AT);
}
