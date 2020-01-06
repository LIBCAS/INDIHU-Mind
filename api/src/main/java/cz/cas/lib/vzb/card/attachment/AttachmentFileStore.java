package cz.cas.lib.vzb.card.attachment;

import core.store.NamedStore;
import org.springframework.stereotype.Repository;

@Repository
public class AttachmentFileStore extends NamedStore<AttachmentFile, QAttachmentFile> {
    public AttachmentFileStore() {
        super(AttachmentFile.class, QAttachmentFile.class);
    }
}
