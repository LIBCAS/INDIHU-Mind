package cz.cas.lib.indihumind.document;

import java.io.InputStream;

/**
 * Interface for classes inheriting from {@link AttachmentFile}.
 *
 * Attachment implementing this interface is meant to be downloaded from application server's file storage.
 */
public interface DownloadableAttachment {

    /**
     * Get attachment ID
     *
     * @return id of attachment
     */
    String getId();

    /**
     * Get name of file.
     *
     * It is used in response header {@code "Content-Disposition":"attachment; filename=" + MY_FILE.getName()}
     *
     * @return file name
     */
    String getName();

    /**
     * Get MIME content type of attachment.
     *
     * It is used in response header {@code "Content-Type": MY_FILE.getContentType()}
     *
     * @return MIME content type of file
     */
    String getContentType();

    /**
     * Sets content stream created from physical file.
     *
     * @param stream of data
     */
    void setStream(InputStream stream);

    /**
     * Gets content stream of physical file.
     *
     * @return stream of data
     */
    InputStream getStream();

    /**
     * Get physical file's size that is stored in DB.
     *
     * It is used to preserve consistency between metadata of physical file and DB
     */
    Long getSize();

    /**
     * Sets physical file's size.
     *
     * It is used to preserve consistency between metadata of physical file and DB
     */
    void setSize(Long size);

    /**
     * Verifying check whether to initialize a download from server's storage.
     *
     * @return true if document is present in server's filesystem
     */
    default boolean shouldBeInServerStorage() {
        return true;
    }

}
