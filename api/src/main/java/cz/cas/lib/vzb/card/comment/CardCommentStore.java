package cz.cas.lib.vzb.card.comment;

import core.store.DatedStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class CardCommentStore extends DatedStore<CardComment, QCardComment> {

    public CardCommentStore() {
        super(CardComment.class, QCardComment.class);
    }

    @Override
    public void delete(CardComment entity) {
        super.hardDelete(entity);
    }

}
