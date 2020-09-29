package cz.cas.lib.vzb.init.builders;

import cz.cas.lib.vzb.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.attachment.ExternalAttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.security.user.User;

import java.time.Instant;

import static core.util.Utils.asSet;

public final class ExternalAttachmentBuilder {
    private final ExternalAttachmentFile externalAttachmentFile;

    private ExternalAttachmentBuilder() {
        externalAttachmentFile = new ExternalAttachmentFile();
    }

    public static ExternalAttachmentBuilder builder() {
        return new ExternalAttachmentBuilder();
    }

    public ExternalAttachmentBuilder provider(AttachmentFileProviderType providerType) {
        externalAttachmentFile.setProviderType(providerType);
        return this;
    }

    public ExternalAttachmentBuilder providerId(String providerId) {
        externalAttachmentFile.setProviderId(providerId);
        return this;
    }

    public ExternalAttachmentBuilder link(String link) {
        externalAttachmentFile.setLink(link);
        return this;
    }

    public ExternalAttachmentBuilder owner(User owner) {
        externalAttachmentFile.setOwner(owner);
        return this;
    }

    public ExternalAttachmentBuilder type(String type) {
        externalAttachmentFile.setType(type);
        return this;
    }

    public ExternalAttachmentBuilder cards(Card... linkedCards) {
        externalAttachmentFile.setLinkedCards(asSet(linkedCards));
        return this;
    }

    public ExternalAttachmentBuilder records(Citation... records) {
        externalAttachmentFile.setRecords(asSet(records));
        return this;
    }

    public ExternalAttachmentBuilder name(String name) {
        externalAttachmentFile.setName(name);
        return this;
    }

    public ExternalAttachmentBuilder created(Instant created) {
        externalAttachmentFile.setCreated(created);
        return this;
    }

    public ExternalAttachmentBuilder updated(Instant updated) {
        externalAttachmentFile.setUpdated(updated);
        return this;
    }

    public ExternalAttachmentBuilder deleted(Instant deleted) {
        externalAttachmentFile.setDeleted(deleted);
        return this;
    }

    public ExternalAttachmentBuilder id(String id) {
        externalAttachmentFile.setId(id);
        return this;
    }

    public ExternalAttachmentFile build() {
        return externalAttachmentFile;
    }
}
