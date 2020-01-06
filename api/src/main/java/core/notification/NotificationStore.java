package core.notification;

import core.index.IndexedDatedStore;
import core.index.IndexedStore;
import lombok.Getter;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link IndexedStore} for storing {@link Notification} and indexing {@link IndexedNotification}.
 */
@Repository
public class NotificationStore extends IndexedDatedStore<Notification, QNotification, IndexedNotification> {

    public NotificationStore() {
        super(Notification.class, QNotification.class, IndexedNotification.class);
    }

    @Getter
    private final String indexType = "notification";

    @Override
    public IndexedNotification toIndexObject(Notification o) {
        IndexedNotification indexed = super.toIndexObject(o);

        indexed.setTitle(o.getTitle());

        indexed.setAuthorName(o.getAuthorName());
        indexed.setAuthorId(o.getAuthorId());
        indexed.setRecipientId(o.getRecipientId());
        indexed.setRecipientName(o.getRecipientName());

        indexed.setFlash(o.getFlash());
        indexed.setRead(o.getRead());
        indexed.setEmailing(o.getEmailing());

        return indexed;
    }
}
