package cz.cas.lib.vzb.card.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.InputStream;

@Entity
@Getter
@Setter
@Table(name = "vzb_local_attachment_file")
public class LocalAttachmentFile extends AttachmentFile {

    private String contentType;
    /**
     * Opened stream to read file content
     *
     * <p>
     * Initialized only if retrieved from {@link AttachmentFileService}.
     * </p>
     */
    @Transient
    @JsonIgnore
    private InputStream stream;

    /**
     * Size of the file content in bytes
     *
     * <p>
     * Initialized only if retrieved from {@link AttachmentFileService}.
     * </p>
     */
    private Long size;

    @Transient
    private final AttachmentFileProviderType providerType = AttachmentFileProviderType.LOCAL;
}
