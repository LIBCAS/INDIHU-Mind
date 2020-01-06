package cz.cas.lib.vzb.card.label;

import core.exception.ForbiddenObject;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.exception.NameAlreadyExistsException;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static core.util.Utils.eq;
import static core.util.Utils.isNull;

@Service
@Slf4j
public class LabelService {

    private LabelStore store;
    private UserDelegate userDelegate;
    private CardStore cardStore;
    private TaskExecutor taskExecutor;

    public void delete(Label entity) {
        List<Card> cardsOfLabel = cardStore.findCardsOfLabel(entity);
        cardsOfLabel.forEach(c -> c.getLabels().remove(entity));
        store.delete(entity);
        log.debug("Deleting label " + entity.getName() + " of user " + entity.getOwner() + ", asynchronously reindexing " + cardsOfLabel.size() + " cards");
        CompletableFuture.runAsync(() -> cardStore.index(cardsOfLabel), taskExecutor);
    }

    public Label find(String id) {
        return store.find(id);
    }

    public Collection<Label> findByUser(String id) {
        return store.findByUser(id);
    }

    public Label save(Label newLabel) {
        Label fromDb = store.find(newLabel.getId());
        if (fromDb != null)
            eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Label.class, newLabel.getId()));

        // enforce addUniqueConstraint `vzb_label_of_user_uniqueness` of columnNames="name,owner_id"
        Label nameExists = store.findEqualNameDifferentId(newLabel);
        isNull(nameExists, () -> new NameAlreadyExistsException(Label.class, nameExists.getId(), nameExists.getName()));

        newLabel.setOwner(userDelegate.getUser());
        store.save(newLabel);
        if (fromDb != null && !StringUtils.equals(fromDb.getName(), newLabel.getName())) {
            List<Card> cardsOfLabel = cardStore.findCardsOfLabel(newLabel);
            log.debug("Updating label " + newLabel.getName() + " of user " + newLabel.getOwner() + ", asynchronously reindexing " + cardsOfLabel.size() + " cards");
            CompletableFuture.runAsync(() -> cardStore.index(cardsOfLabel), taskExecutor);
        }
        return newLabel;
    }

    @Inject
    public void setStore(LabelStore store) {
        this.store = store;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
    }

    @Inject
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
