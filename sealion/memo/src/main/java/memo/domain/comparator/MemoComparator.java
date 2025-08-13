package memo.domain.comparator;

import common.domain.entity.*;
import jakarta.inject.Singleton;

import java.util.Comparator;
import java.util.stream.Collectors;

@Singleton
public class MemoComparator {

    // specific
    // memo type
    public static final Comparator<Memo> BY_MEMO_TYPE =
            Comparator.comparing(memo -> memo.getMemoType().getDetail(), String.CASE_INSENSITIVE_ORDER);

    // basic
    public static final Comparator<Memo> BY_DETAIL =
            Comparator.comparing(Memo::getDetail, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Memo> BY_CREATED_AT =
            Comparator.comparing(Memo::getCreatedAt);
}
