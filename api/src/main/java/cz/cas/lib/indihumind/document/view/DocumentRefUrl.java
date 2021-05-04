package cz.cas.lib.indihumind.document.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.cas.lib.indihumind.document.UrlAttachmentFile;
import lombok.Getter;

import javax.persistence.*;

/**
 * @see UrlAttachmentFile
 */
//@Immutable - superclass must have Immutable annotation
@Getter
@Entity(name = "document_ref_url")
@Table(name = "vzb_attachment_url")
@DiscriminatorValue("URL")
@JsonPropertyOrder({"id", "name", "providerType", "location", "link", "type", "created", "updated", "deleted"})
public class DocumentRefUrl extends DocumentRef {

    @Enumerated(value = EnumType.STRING)
    private UrlAttachmentFile.UrlDocumentLocation location;
    private String link;

    @Override
    public UrlAttachmentFile toEntity() {
        UrlAttachmentFile entity = super.toEntity(new UrlAttachmentFile());
        entity.setLocation(location);
        entity.setLink(link);
        return entity;
    }

}
