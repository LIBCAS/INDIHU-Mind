package cz.cas.lib.indihumind.document.view;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.document.AttachmentFileStore;
import cz.cas.lib.indihumind.document.IndexedAttachmentFile;
import org.springframework.stereotype.Repository;

/**
 * @see AttachmentFileStore
 */
@Repository
public class DocumentListDtoStore extends IndexedDatedStore<DocumentListDto, QDocumentListDto, IndexedAttachmentFile> {

    public DocumentListDtoStore() {
        super(DocumentListDto.class, QDocumentListDto.class, IndexedAttachmentFile.class);
    }

    @Override
    public String getIndexType() {
        return AttachmentFileStore.INDEX_TYPE;
    }

}
