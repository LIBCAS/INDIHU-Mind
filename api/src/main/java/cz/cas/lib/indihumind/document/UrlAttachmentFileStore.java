package cz.cas.lib.indihumind.document;

import core.store.DatedStore;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cz.cas.lib.indihumind.document.UrlAttachmentFile.UrlDocumentLocation.SERVER;


@Repository
public class UrlAttachmentFileStore extends DatedStore<UrlAttachmentFile, QUrlAttachmentFile> {
    public UrlAttachmentFileStore() {
        super(UrlAttachmentFile.class, QUrlAttachmentFile.class);
    }

    public Set<String> allUrlAttachmentsIds() {
        List<String> fetch = query()
                .select(qObject().id)
                .fetch();
        detachAll();
        return new HashSet<>(fetch);
    }

    public Long urlAttachmentsSizeForUser(String userId) {
        Long fetch = query()
                .select(qObject().size.sum())
                .where(qObject().owner.id.eq(userId).and(qObject().location.eq(SERVER)))
                .fetchOne();
        if (fetch == null)
            fetch = 0L;
        detachAll();
        return fetch;
    }

}
