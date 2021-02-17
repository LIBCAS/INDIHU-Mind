package core.file;

import core.domain.DatedObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.InputStream;

/**
 * Reference to a file stored in {@link FileRepository}.
 *
 * Should be created only through {@link FileRepository} and not cascaded from another entity.
 */
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "uas_file")
public class FileRef extends DatedObject {

    /** Filename */
    protected String name;

    /** MIME type */
    private String contentType;

    /** Indexing status */
    private Boolean indexedContent;

    /**
     * Opened stream to read file content
     *
     * Initialized only if retrieved from {@link FileRepository}.
     */
    @Transient
    private InputStream stream;

    /**
     * Text representation of file content
     *
     * Initialized only if saved through {@link FileRepository}.
     */
    @Transient
    private String content;

    /**
     * Size of the file content
     *
     * Initialized only if retrieved from {@link FileRepository}.
     */
    @Transient
    private Long size;

}
