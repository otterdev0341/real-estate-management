package property.domain.comparator;

import common.domain.entity.ContactType;
import common.domain.entity.PropertyStatus;

import java.util.Comparator;

public class PropertyStatusComparators {
    public static final Comparator<PropertyStatus> BY_DETAIL =
            Comparator.comparing(PropertyStatus::getDetail, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<PropertyStatus> BY_CREATED_AT =
            Comparator.comparing(PropertyStatus::getCreatedAt);


}
