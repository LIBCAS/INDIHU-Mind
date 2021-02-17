package cz.cas.lib.indihumind.document;

import core.store.DatedStore;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class LocalAttachmentFileStore extends DatedStore<LocalAttachmentFile, QLocalAttachmentFile> {
    public LocalAttachmentFileStore() {
        super(LocalAttachmentFile.class, QLocalAttachmentFile.class);
    }

    public Set<String> allLocalAttachmentsIds() {
        List<String> fetch = query()
                .select(qObject().id)
                .fetch();
        detachAll();
        return new HashSet<>(fetch);
    }

    public Long localAttachmentsSizeForUser(String userId) {
        Long fetch = query()
                .select(qObject().size.sum())
                .where(qObject().owner.id.eq(userId))
                .fetchOne();
        if (fetch == null)
            fetch = 0L;
        detachAll();
        return fetch;
    }
}
