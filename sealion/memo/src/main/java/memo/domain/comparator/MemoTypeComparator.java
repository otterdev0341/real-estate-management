package memo.domain.comparator;

import common.domain.entity.ContactType;
import common.domain.entity.MemoType;
import jakarta.inject.Singleton;

import java.util.Comparator;

@Singleton
public class MemoTypeComparator {

    public static final Comparator<MemoType> BY_DETAIL =
            Comparator.comparing(MemoType::getDetail, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<MemoType> BY_CREATED_AT =
            Comparator.comparing(MemoType::getCreatedAt);

}


