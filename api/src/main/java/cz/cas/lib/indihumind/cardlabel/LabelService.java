package cz.cas.lib.indihumind.cardlabel;

import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.exception.NameAlreadyExistsException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;
import static cz.cas.lib.indihumind.exception.NameAlreadyExistsException.ErrorCode.NAME_ALREADY_EXISTS;

@Service
@Slf4j
public class LabelService {

    private LabelStore store;
    private CardStore cardStore;
    private TaskExecutor taskExecutor;
    private UserDelegate userDelegate;


    public Label find(String id) {
        Label entity = store.find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, Label.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Label.class, id));
        return store.find(id);
    }

    public List<Label> findAllOfUser(String id) {
        Collection<Label> labels = store.findByUser(id);
        List<Label> result = new ArrayList<>(labels);
        result.sort(Comparator.comparingInt(Label::getOrdinalNumber));
        return result;
    }

    @Transactional
    public Label save(Label newLabel) {
        Label fromDb = store.find(newLabel.getId());
        if (fromDb != null)
            eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Label.class, newLabel.getId()));

        // enforce addUniqueConstraint `vzb_label_of_user_uniqueness` of columnNames="name,owner_id"
        newLabel.setOwner(userDelegate.getUser());
        Label nameExists = store.findEqualNameDifferentId(newLabel);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), Label.class, nameExists.getId(), nameExists.getOwner()));

        Label saved = store.save(newLabel);

        // Label.name is indexed in card core so relevant documents must be reindexed if name has changed
        if (fromDb != null && !StringUtils.equals(fromDb.getName(), newLabel.getName())) {
            List<Card> cardsOfLabel = cardStore.findCardsOfLabel(newLabel);
            log.debug("Updating label " + newLabel.getName() + " of user " + newLabel.getOwner() + ", asynchronously reindexing " + cardsOfLabel.size() + " cards");
            CompletableFuture.runAsync(() -> cardStore.index(cardsOfLabel), taskExecutor);
        }

        return saved;
    }

    @Transactional
    public void delete(String id) {
        Label entity = this.find(id);

        List<Card> cardsOfLabel = cardStore.findCardsOfLabel(entity);
        cardsOfLabel.forEach(c -> c.getLabels().remove(entity));
        store.delete(entity);

        // Label.name is indexed in card core so relevant documents must be reindexed
        log.debug("Deleting label " + entity.getName() + " of user " + entity.getOwner() + ", asynchronously reindexing " + cardsOfLabel.size() + " cards");
        CompletableFuture.runAsync(() -> cardStore.index(cardsOfLabel), taskExecutor);
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
