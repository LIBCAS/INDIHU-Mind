package cz.cas.lib.indihumind.document.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cz.cas.lib.indihumind.document.ExternalAttachmentFile;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @see ExternalAttachmentFile
 */
//@Immutable - superclass must have Immutable annotation
@Getter
@Entity(name = "document_external_ref")
@Table(name = "vzb_attachment_external")
@DiscriminatorValue("EXTERNAL")
@JsonPropertyOrder({"id", "name", "providerType", "providerId", "link", "type", "created", "updated", "deleted"})
public class DocumentRefExternal extends DocumentRef {

    private String providerId;
    private String link;

    @Override
    public ExternalAttachmentFile toEntity() {
        ExternalAttachmentFile entity = super.toEntity(new ExternalAttachmentFile());
        entity.setProviderId(providerId);
        entity.setLink(link);
        return entity;
    }
}
