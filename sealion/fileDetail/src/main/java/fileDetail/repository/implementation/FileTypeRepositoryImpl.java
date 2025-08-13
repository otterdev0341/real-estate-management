package fileDetail.repository.implementation;

import com.spencerwi.either.Either;
import common.domain.entity.FileType;
import common.errorStructure.RepositoryError;
import fileDetail.repository.internal.InternalFileTypeRepository;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@ApplicationScoped
public class FileTypeRepositoryImpl implements PanacheRepositoryBase<FileType, UUID>, InternalFileTypeRepository {


    @Override
    public Either<RepositoryError, FileType> getFileTypeByFileWithExtension(String fileNameWithExtension) {
        try {
            Either<RepositoryError, FileType> target = getFileTypeFileName(fileNameWithExtension.trim());
            if (target.isRight()) {
                return Either.right(target.getRight());
            } else {
                return Either.left(new RepositoryError.FetchFailed("FileType not found for extension: " + fileNameWithExtension));
            }
        } catch (Exception e) {
            return Either.left(new RepositoryError.FetchFailed("FileType not found for extension cause by " + e.getMessage()));
        }





    }

    @Override
    public Either<RepositoryError, FileType> getPdfFileType() {
        Optional<FileType> fileType = find("detail = ?1", "pdf").firstResultOptional();
        return fileType.<Either<RepositoryError, FileType>>map(Either::right)
                .orElseGet(() -> Either.left(new RepositoryError.FetchFailed("FileType not found for extension: pdf")));
    }

    @Override
    public Either<RepositoryError, FileType> getImageFileType() {
        Optional<FileType> fileType = find("detail = ?1", "image").firstResultOptional();
        return fileType.<Either<RepositoryError, FileType>>map(Either::right)
                .orElseGet(() -> Either.left(new RepositoryError.FetchFailed("FileType not found for extension: pdf")));
    }

    @Override
    public Either<RepositoryError, FileType> getOtherFileType() {
        Optional<FileType> fileType = find("detail = ?1", "other").firstResultOptional();
        return fileType.<Either<RepositoryError, FileType>>map(Either::right)
                .orElseGet(() -> Either.left(new RepositoryError.FetchFailed("FileType not found for extension: pdf")));
    }

    private Either<RepositoryError, FileType> getFileTypeFileName(String fileName) {
        Optional<String> ext = getFileExtension(fileName.toLowerCase().trim());
        if( ext.isEmpty()) {
            return Either.left(new RepositoryError.FetchFailed("FileType not found for extension: " + fileName));
        }
        String theExtention = ext.get();

        // Image file types
        if (theExtention.matches("^(jpg|jpeg|png|gif|svg|webp|image)$")) {
            return getImageFileType();
        }

        // PDF files
        if (theExtention.equals("pdf")) {
            return getPdfFileType();
        }

        // All other file types
        return getOtherFileType();
    }

    private  Optional<String> getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return Optional.empty();
        }

        // Regex to match a dot followed by one or more word characters at the end of the string.
        Pattern pattern = Pattern.compile("\\.([a-zA-Z0-9]+)$");
        Matcher matcher = pattern.matcher(filename);

        if (matcher.find()) {
            // Group 1 contains the text matched inside the parentheses, which is the extension.
            return Optional.of(matcher.group(1));
        }

        return Optional.empty();
    }



}
