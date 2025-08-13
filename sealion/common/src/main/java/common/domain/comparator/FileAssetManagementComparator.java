package common.domain.comparator;

import common.domain.entity.Memo;
import common.service.declare.fileAssetManagement.HasFileDetails;

import java.util.Comparator;
import java.util.stream.Collectors;

public class FileAssetManagementComparator<T extends HasFileDetails> {

    private FileAssetManagementComparator() {
    }

    public static <T extends HasFileDetails> FileAssetManagementComparator<T> of(Class<T> entityClass) {
        return new FileAssetManagementComparator<>();
    }



    public final Comparator<T> BY_FILE_IMAGE =
            Comparator.comparing(
                    // This lambda checks if the entity has any "other" files.
                    // It returns 'true' if an "other" file exists, 'false' otherwise.
                    targetEntity -> targetEntity.getFileDetails().stream()
                            .anyMatch(fileDetail -> fileDetail.getType().getDetail().equalsIgnoreCase("image"))
                    ,
                    // We use naturalOrder() and then reverse it.
                    // This places 'true' (entities with "other" files) before 'false'.
                    Comparator.reverseOrder()
            );

    public final Comparator<T> BY_FILE_PDF =
            Comparator.comparing(
                    // This lambda checks if the entity has any "other" files.
                    // It returns 'true' if an "other" file exists, 'false' otherwise.
                    targetEntity -> targetEntity.getFileDetails().stream()
                            .anyMatch(fileDetail -> fileDetail.getType().getDetail().equalsIgnoreCase("pdf"))
                    ,
                    // We use naturalOrder() and then reverse it.
                    // This places 'true' (entities with "other" files) before 'false'.
                    Comparator.reverseOrder()
            );

    public final Comparator<T> BY_FILE_OTHER =
            Comparator.comparing(
                    // This lambda checks if the entity has any "other" files.
                    // It returns 'true' if an "other" file exists, 'false' otherwise.
                    targetEntity -> targetEntity.getFileDetails().stream()
                            .anyMatch(fileDetail -> fileDetail.getType().getDetail().equalsIgnoreCase("other"))
                    ,
                    // We use naturalOrder() and then reverse it.
                    // This places 'true' (entities with "other" files) before 'false'.
                    Comparator.reverseOrder()
            );
}
