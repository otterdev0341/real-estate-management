package property.domain.comparator;

import common.domain.entity.ContactType;
import common.domain.entity.PropertyType;

import java.util.Comparator;

public class PropertyTypeComparators {
    public static final Comparator<PropertyType> BY_DETAIL =
            Comparator.comparing(PropertyType::getDetail, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<PropertyType> BY_CREATED_AT =
            Comparator.comparing(PropertyType::getCreatedAt);


}
