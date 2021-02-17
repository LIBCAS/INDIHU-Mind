package cz.cas.lib.indihumind.document;

import core.index.IndexedDatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class AttachmentFileStore extends IndexedDatedStore<AttachmentFile, QAttachmentFile, IndexedAttachmentFile> {
    public final String indexType = "attachmentFile";

    public AttachmentFileStore() {
        super(AttachmentFile.class, QAttachmentFile.class, IndexedAttachmentFile.class);
    }

    @Override
    public String getIndexType() {
        return indexType;
    }

    @Override
    public IndexedAttachmentFile toIndexObject(AttachmentFile obj) {
        IndexedAttachmentFile file = super.toIndexObject(obj);
        if (obj.getOwner() != null) file.setUserId(obj.getOwner().getId());
        if (obj.getName() != null) file.setName(obj.getName());
        file.setProviderType(obj.getProviderType().name());
        return file;
    }

}
