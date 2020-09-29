package core.mail;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for mail attachment.
 */
@AllArgsConstructor
@Getter
@Setter
public class MailAttachment {
    /**
     * Name of the file.
     */
    protected String name;

    /**
     * Binary representation of the file.
     */
    protected byte[] content;

    /**
     * MIME content type of the file.
     */
    protected String contentType;
}
