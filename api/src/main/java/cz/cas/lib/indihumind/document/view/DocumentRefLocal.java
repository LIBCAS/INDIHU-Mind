package cz.cas.lib.indihumind.document.view;

import cz.cas.lib.indihumind.document.ExternalAttachmentFile;
import cz.cas.lib.indihumind.document.LocalAttachmentFile;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @see LocalAttachmentFile
 */
//@Immutable - superclass must have Immutable annotation
@Getter
@Entity(name = "document_ref_local")
@Table(name = "vzb_attachment_url")
@DiscriminatorValue("LOCAL")
public class DocumentRefLocal extends DocumentRef {

    @Override
    public LocalAttachmentFile toEntity() {
        return super.toEntity(new LocalAttachmentFile());
    }

}
