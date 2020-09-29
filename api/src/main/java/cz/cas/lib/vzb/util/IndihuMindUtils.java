package cz.cas.lib.vzb.util;

import com.google.common.net.HttpHeaders;
import core.domain.DomainObject;
import core.exception.BadArgument;
import core.exception.GeneralException;
import cz.cas.lib.vzb.attachment.validation.ForbiddenFile;
import cz.cas.lib.vzb.exception.ContentLengthRequiredException;
import cz.cas.lib.vzb.exception.ForbiddenFileException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Fragment methods used by services to hide low-level handling from core @Service classes.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndihuMindUtils {

    /**
     * Used to encode authors' first and last names into single String.
     * Be stores only a single value, therefore there was a need to combine two values (first and last name) into a
     * single String. This encoding is then used to parse single value into two.
     */
    public static final String AUTHOR_NAME_ENCODING = "#&&#";

    /**
     * Determines whether entities from two collections are the same (comparing by ID).
     *
     * @param currentCollection collection of ID entities
     * @param newCollection     collection of IDs, usually from DTO
     * @return true if both collection contain the same IDs
     */
    public static boolean isCollectionWithoutModification(Collection<? extends DomainObject> currentCollection, Collection<String> newCollection) {
        return newCollection.size() == currentCollection.size()
                && newCollection.containsAll(currentCollection.stream().map(DomainObject::getId).collect(Collectors.toSet()));
    }

    public static boolean isCollectionModified(Collection<? extends DomainObject> currentCollection, Collection<String> newCollection) {
        return !isCollectionWithoutModification(currentCollection, newCollection);
    }

    public static URL createUrlFromLink(String link) {
        try {
            return new URL(link);
        } catch (MalformedURLException e) {
            throw new BadArgument(URL.class, "Provided URL link can not be parsed.");
        }
    }

    public static int getSizeFromUrlFile(URL fileUrl) {
        int sizeBytes;
        try {
            URLConnection urlConnection = fileUrl.openConnection();
            sizeBytes = urlConnection.getContentLength();
            urlConnection.getInputStream().close();
            if (sizeBytes < 0)
                throw new ContentLengthRequiredException("URL file's content-length header not available.");
        } catch (IOException e) {
            throw new GeneralException("File stream has malfunctioned.");
        }
        return sizeBytes;
    }

    public static void checkUrlFileExtension(URL url) {
        try {
            String fileNameFromUrl = Paths.get(url.getPath()).getFileName().toString();
            checkFileExtensionFromName(fileNameFromUrl);
        } catch (InvalidPathException e) {
            throw new BadArgument(URL.class, "File name and its extension can not be parsed from URL's path");
        }
    }

    public static void checkFileExtensionFromName(String fullFileName) {
        if (ForbiddenFile.isFileExtensionForbidden(fullFileName))
            throw new ForbiddenFileException(fullFileName);
    }

    public static ResponseEntity<InputStreamResource> createResponseEntityPdfFile(InputStream generatedPdfFile, String fileName) {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(generatedPdfFile));
    }

    public static String prettyPrintCollectionIds(Collection<? extends DomainObject> collection) {
        if (collection == null) return "";
        return collection.stream().map(DomainObject::getId).collect(Collectors.joining(", "));
    }

    public static String escapeText(String originalText) {
        if (originalText == null) {
            return null;
        }
        StringBuilder escapedText = new StringBuilder();
        char currentChar;

        for (int i = 0; i < originalText.length(); i++) {
            currentChar = originalText.charAt(i);
            switch (currentChar) {
                case '<':
                    escapedText.append("&lt;");
                    break;
                case '>':
                    escapedText.append("&gt;");
                    break;
                case '&':
                    escapedText.append("&amp;");
                    break;
                case '"':
                    escapedText.append("&quot;");
                    break;
                case '\'':
                    escapedText.append("&#8216;");
                    break;
                case '¢':
                    escapedText.append("&#162;");
                    break;
                case '£':
                    escapedText.append("&#163;");
                    break;
                case '€':
                    escapedText.append("&#8364;");
                    break;
                case '§':
                    escapedText.append("&#167;");
                    break;
                case '©':
                    escapedText.append("&#169;");
                    break;
                case '®':
                    escapedText.append("&#174;");
                    break;
                case '°':
                    escapedText.append("&#176;");
                    break;
                default: // append unchanged char to final escaped text
                    escapedText.append(currentChar);
            }
        }
        return escapedText.toString();
    }
}
