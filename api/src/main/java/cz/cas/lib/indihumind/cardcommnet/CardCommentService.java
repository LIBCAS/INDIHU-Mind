package cz.cas.lib.indihumind.cardcommnet;

import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardService;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.eq;
import static core.util.Utils.notNull;

@Service
public class CardCommentService {

    private CardService cardService;
    private CardStore cardStore;
    private CardCommentStore store;
    private UserDelegate userDelegate;


    public CardComment find(String id) {
        CardComment comment = store.find(id);
        notNull(comment, () -> new MissingObject(ENTITY_IS_NULL, CardComment.class, id));
        notNull(comment.getCard(), () -> new MissingObject(ENTITY_IS_NULL, "Comment does not belong to a card"));
        eq(comment.getCard().getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, CardComment.class, id));
        return comment;
    }

    @Transactional
    public CardComment create(CardCommentCreateDto dto) {
        Card card = cardService.find(dto.getCardId());
        int nextCommentOrdinalNum = card.getComments().size(); // first comment has ordinal number #0

        CardComment comment = new CardComment();
        comment.setText(dto.getText());
        comment.setOrdinalNumber(nextCommentOrdinalNum);
        comment.setCard(card);
        CardComment fromDb = store.save(comment);

        fromDb.setTextUpdated(fromDb.getUpdated());

        return store.save(fromDb);
    }

    @Transactional
    public CardComment updateText(CardCommentUpdateDto dto) {
        CardComment comment = find(dto.getId());

        comment.setText(dto.getText());
        comment.setTextUpdated(Instant.now());

        return store.save(comment);
    }

    @Transactional
    public void delete(String commentId) {
        CardComment commentFromDb = find(commentId);
        Card card = commentFromDb.getCard();

        List<CardComment> otherCardComments = card.getComments();
        otherCardComments.remove(commentFromDb);
        cardStore.save(card); // save card to trigger orphan removal -> deletes comment

        for (int index = 0; index < otherCardComments.size(); index++) { // change ordinal numbers
            CardComment commentAtIndex = otherCardComments.get(index);
            commentAtIndex.setOrdinalNumber(index);
        }
        store.save(otherCardComments);
    }

    public Collection<CardComment> findByCard(String cardId) {
        Card card = cardService.find(cardId);
        return card.getComments();
    }


    @Inject
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    @Inject
    public void setStore(CardCommentStore store) {
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
}
