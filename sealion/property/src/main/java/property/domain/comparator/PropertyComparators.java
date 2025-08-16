package property.domain.comparator;

import common.domain.entity.Property;
import common.domain.entity.PropertyStatus;

import java.util.Comparator;

public class PropertyComparators {
    // specific

    /**
     * Compares properties based on their 'sold' status.
     * Properties that are not sold (false) come before properties that are sold (true).
     */
    public static final Comparator<Property> BY_SOLD_STATUS =
            Comparator.comparing(Property::getSold);

    /**
     * Compares properties based on the 'FSP' (Final Selling Price) value.
     * Compares them in ascending order.
     */
    public static final Comparator<Property> BY_FSP =
            Comparator.comparing(
                    Property::getFsp,
                    Comparator.nullsFirst(Comparator.naturalOrder())
            );

    /**
     * Compares properties based on the 'detail' of their PropertyStatus.
     * Sorts alphabetically by the status detail string, case-insensitively.
     */
    public static final Comparator<Property> BY_PROPERTY_STATUS =
            Comparator.comparing(
                    p -> p.getStatus().getDetail(),
                    String.CASE_INSENSITIVE_ORDER
            );

    /**
     * Compares properties based on the business name of the owner.
     * Handles potential nulls in the ownerBy field gracefully.
     */
    public static final Comparator<Property> BY_OWNER =
            Comparator.comparing(
                    p -> p.getOwnerBy().getBusinessName(),
                    Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)
            );


    // basic
    public static final Comparator<Property> BY_Name =
            Comparator.comparing(Property::getName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Property> BY_CREATED_AT =
            Comparator.comparing(Property::getCreatedAt);



}
