package cz.cas.lib.vzb.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

/**
 * Attachment file downloaded from user-provided URL.
 */
@Getter
@Setter
@Table(name = "vzb_attachment_url")
@DiscriminatorValue("URL")
@Entity
public class UrlAttachmentFile extends AttachmentFile implements DownloadableAttachment {

    @Transient
    private final AttachmentFileProviderType providerType = AttachmentFileProviderType.URL;

    /** URL link from which file has been downloaded **/
    @NotNull
    private String link;

    /** Content type of the file e.g. {@link org.springframework.http.MediaType#IMAGE_PNG_VALUE} **/
    private String contentType;

    /**
     * Opened stream to read file content, used to download a file from application server's file storage.
     *
     * Initialized only if retrieved from {@link AttachmentFileService}.
     */
    @Transient
    @JsonIgnore
    private InputStream stream;

    /**
     * Size of the file content in <b>bytes</b>
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long size;

}
