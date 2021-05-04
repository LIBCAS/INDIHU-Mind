package cz.cas.lib.indihumind.document;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.util.Reindexable;
import org.springframework.stereotype.Repository;

@Repository
public class AttachmentFileStore extends IndexedDatedStore<AttachmentFile, QAttachmentFile, IndexedAttachmentFile> implements Reindexable {

    public AttachmentFileStore() {
        super(AttachmentFile.class, QAttachmentFile.class, IndexedAttachmentFile.class);
    }

    public static final String INDEX_TYPE = "attachmentFile";

    @Override
    public String getIndexType() {
        return INDEX_TYPE;
    }

    @Override
    public IndexedAttachmentFile toIndexObject(AttachmentFile obj) {
        IndexedAttachmentFile file = super.toIndexObject(obj);
        if (obj.getOwner() != null) file.setUserId(obj.getOwner().getId());
        if (obj.getName() != null) file.setName(obj.getName());
        file.setProviderType(obj.getProviderType().name());
        return file;
    }

    @Override
    public void reindexEverything() {
        dropReindex();
    }

    @Override
    public void removeAllDataFromIndex() {
        removeAllIndexes();
    }
}
