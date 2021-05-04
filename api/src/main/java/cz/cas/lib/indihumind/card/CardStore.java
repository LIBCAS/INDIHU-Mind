package cz.cas.lib.indihumind.card;

import core.domain.DomainObject;
import core.index.IndexedDatedStore;
import core.store.DomainStore;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcontent.view.CardContentListDto;
import cz.cas.lib.indihumind.cardcontent.view.CardContentListDtoStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.document.view.DocumentRef;
import cz.cas.lib.indihumind.util.Reindexable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static core.util.Utils.toDate;

/**
 * Indexed repository for indexed {@link IndexedCard} and class {@link Card}
 */
@Slf4j
@Repository
public class CardStore extends IndexedDatedStore<Card, QCard, IndexedCard> implements Reindexable {

    public CardStore() {
        super(Card.class, QCard.class, IndexedCard.class);
    }

    public static final String INDEX_TYPE = "card";

    @Override
    public String getIndexType() {
        return INDEX_TYPE;
    }

    private CardContentListDtoStore cardContentListDtoStore;
    private TaskExecutor taskExecutor;


    /**
     * Query Card with initialized CardNote entity.
     */
    public Card findWithCardNote(String cardId) {
        Card fetch = query()
                .select(qObject())
                .where(qObject().id.eq(cardId).and(qObject().deleted.isNull()))
                .join(qObject().structuredNote).fetchJoin()
                .fetchFirst();
        detachAll();
        return fetch;
    }

    public Long cardNotesSizeForUser(String userId) {
        QCardNote qCardNote = QCardNote.cardNote;
        QCard qCard = qObject();
        Long fetch = query()
                .select(qCardNote.size.sum())
                .where(qObject().owner.id.eq(userId))
                .from(qCard)
                .innerJoin(qCard.structuredNote, qCardNote)
                .fetchOne();
        if (fetch == null)
            fetch = 0L;
        detachAll();
        return fetch;
    }

    public Long cardNoteSizeForCard(String cardId) {
        QCardNote qCardNote = QCardNote.cardNote;
        QCard qCard = qObject();

        Long fetch = query()
                .select(qCardNote.size)
                .where(qObject().id.eq(cardId))
                .from(qCard)
                .innerJoin(qCard.structuredNote, qCardNote)
                .fetchOne();

        if (fetch == null)
            fetch = 0L;
        detachAll();
        return fetch;
    }

    public List<Card> findCardsFromTrashBin(String userId) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().owner.id.eq(userId)
                        .and(qObject().deleted.isNull())
                        .and(qObject().status.eq(Card.CardStatus.TRASHED))
                )
                .fetch();
        detachAll();
        return fetch;
    }

    public List<Card> findCardsOfCategory(Category cat) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().categories.contains(cat))
                .fetch();
        detachAll();
        return fetch;
    }

    public List<Card> findCardsOfLabel(Label label) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().labels.contains(label))
                .fetch();
        detachAll();
        return fetch;
    }

    public void asynchronousReindex(Collection<CardRef> collection) {
        CompletableFuture.runAsync(() -> {
            log.debug("Async Reindexing '{}' cards...", collection.size());
            List<Card> fetched = findAllInList(collection.stream().map(DomainObject::getId).collect(Collectors.toList()));
            index(fetched);
            log.debug("Async Reindexing of cards is completed.");
        }, taskExecutor);
    }


    /**
     * Copy-pasted {@link DomainStore#save}
     */
    public Collection<? extends Card> saveWithoutIndex(Collection<Card> entities) {
        Set<Card> saved = entities.stream()
                .map(entityManager::merge)
                .collect(Collectors.toSet());

        entityManager.flush();
        detachAll();

        entities.forEach(this::logSaveEvent);

        return saved;
    }

    @Override
    public IndexedCard toIndexObject(Card obj) {
        IndexedCard indexed = super.toIndexObject(obj);
        if (obj.getOwner() != null) indexed.setUserId(obj.getOwner().getId());
        indexed.setPid(obj.getPid());
        if (obj.getName() != null) indexed.setName(obj.getName());
        if (obj.getRawNote() != null) indexed.setNote(obj.getRawNote());
        indexed.setStatus(obj.getStatus().name());

        if (!obj.getDocuments().isEmpty()) {
            indexed.setDocuments(obj.getDocuments().stream()
                    .map(DocumentRef::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        if (!obj.getCategories().isEmpty()) {
            indexed.setCategories(obj.getCategories().stream()
                    .map(Category::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        if (!obj.getCategories().isEmpty()) {
            indexed.setCategoryIds(obj.getCategories().stream()
                    .map(Category::getId)
                    .collect(Collectors.toList()));
        }

        if (!obj.getLabels().isEmpty()) {
            indexed.setLabels(obj.getLabels().stream()
                    .map(Label::getName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }
        if (!obj.getLabels().isEmpty()) {
            indexed.setLabelIds(obj.getLabels().stream()
                    .map(Label::getId)
                    .collect(Collectors.toList()));
        }

        CardContentListDto latestContent = cardContentListDtoStore.findLastVersionOfCard(obj.getId());
        if (latestContent != null) {
            indexed.setContentCreated(toDate(latestContent.getCreated()));
            indexed.setContentUpdated(toDate(latestContent.getUpdated()));
            if (!latestContent.getAttributes().isEmpty()) {
                indexed.setAttributes(latestContent.getAttributes().stream()
                        .filter(attribute -> attribute.getType() == AttributeType.STRING)
                        .map(attribute -> (String) attribute.getValue())
                        .collect(Collectors.toList()));
            }
        }

        return indexed;
    }


    @Override
    public void reindexEverything() {
        dropReindex();
    }

    @Override
    public void removeAllDataFromIndex() {
        removeAllIndexes();
    }

    @Inject
    public void setCardContentListDtoStore(CardContentListDtoStore cardContentListDtoStore) {
        this.cardContentListDtoStore = cardContentListDtoStore;
    }

    @Inject
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
