package cz.cas.lib.indihumind.document.view;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.document.AttachmentFileStore;
import cz.cas.lib.indihumind.document.IndexedAttachmentFile;
import org.springframework.stereotype.Repository;


/**
 * @see AttachmentFileStore
 */
@Repository
public class DocumentRefStore extends IndexedDatedStore<DocumentRef, QDocumentRef, IndexedAttachmentFile> {

    public DocumentRefStore() {
        super(DocumentRef.class, QDocumentRef.class, IndexedAttachmentFile.class);
    }

    @Override
    public String getIndexType() {
        return AttachmentFileStore.INDEX_TYPE;
    }

}
