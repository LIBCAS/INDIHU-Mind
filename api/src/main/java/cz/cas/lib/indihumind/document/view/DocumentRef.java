package cz.cas.lib.indihumind.document.view;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import core.domain.DatedObject;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.document.AttachmentFileProviderType;
import cz.cas.lib.indihumind.util.projection.EntityProjection;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;

/**
 * @see AttachmentFile
 */
@Immutable
@Getter
@Entity(name = "document_ref")
@DiscriminatorColumn(name = "disc_type")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "vzb_attachment_abstract")
@JsonPropertyOrder({"id", "name", "providerType", "type", "created", "updated", "deleted"})
public abstract class DocumentRef extends DatedObject implements EntityProjection<AttachmentFile> {

    @Enumerated(value = EnumType.STRING)
    protected AttachmentFileProviderType providerType;
    protected String name;
    protected String type;


    protected DocumentRef() {
    }

    public <ENTITY extends AttachmentFile> ENTITY toEntity(ENTITY entity) {
        entity.setId(id);
        entity.setCreated(created);
        entity.setUpdated(updated);
        entity.setDeleted(deleted);

        entity.setName(name);
        entity.setType(type);
        entity.setProviderType(providerType);
        return entity;
    }

}
