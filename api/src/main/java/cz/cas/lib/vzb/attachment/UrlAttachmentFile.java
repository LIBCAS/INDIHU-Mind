package cz.cas.lib.vzb.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    /** URL link from which file can be downloaded **/
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

    /**
     * Defines choice of user whether to store URL file or only keep a link to the document.
     */
    @Enumerated(value = EnumType.STRING)
    UrlDocumentLocation location = UrlDocumentLocation.WEB;


    @Override
    public boolean shouldBeInServerStorage() {
        return location == UrlDocumentLocation.SERVER;
    }

    public enum UrlDocumentLocation {
        /** URL file is downloaded to storage; download action in FE should transfer file */
        SERVER,
        /** URL file is only referenced by link and not stored in server's storage; FE only opens a link in browser */
        WEB
    }
}
