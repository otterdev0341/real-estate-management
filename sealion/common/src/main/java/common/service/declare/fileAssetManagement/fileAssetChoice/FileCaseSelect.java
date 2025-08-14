package common.service.declare.fileAssetManagement.fileAssetChoice;

import com.spencerwi.either.Either;
import common.domain.entity.FileDetail;

import java.util.Optional;

public enum FileCaseSelect {
    IMAGE,
    PDF,
    OTHER,
    ALL;

    /**
     * Finds the FileCaseSelect enum constant that matches the given string, ignoring case.
     *
     * @param value The string to match (e.g., "image", "pdf", "all").
     * @return The corresponding FileCaseSelect enum, or null if no match is found.
     */
    public static Either<String, FileCaseSelect> fromString(String value) {
        if (value == null) {
            return Either.left("can't parse string to enum reason value is null");
        }
        for (FileCaseSelect fileCase : FileCaseSelect.values()) {
            if (fileCase.name().equalsIgnoreCase(value.trim())) {
                return Either.right(fileCase);
            }
        }
        return Either.left("Error while parsing string to FileCaseSelect: no matching constant found for '" + value + "'");
    }

    // Helper method to filter files by type
    public static boolean fileCaseMatches(FileDetail fileDetail, FileCaseSelect fileCase) {
        String fileTypeDetail = fileDetail.getType().getDetail().toLowerCase();
        return switch (fileCase) {
            case IMAGE -> fileTypeDetail.equals("image");
            case PDF -> fileTypeDetail.equals("pdf");
            case OTHER -> fileTypeDetail.equals("other");
            default -> false;
        };


    }
}
