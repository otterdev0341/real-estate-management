package common.service.declare.fileAssetManagement.fileAssetChoice;

import com.spencerwi.either.Either;

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
        try {
            return Either.right(FileCaseSelect.valueOf(value.toLowerCase()));
        } catch (Exception e) {
            // No matching enum constant found
            return Either.left("Error while parsing string to FileCaseSelect by: " + e.getMessage());
        }
    }

    /**
    * Returns the name of this enum constant.
    *
    * @return The name of the enum constant.
    */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

}
