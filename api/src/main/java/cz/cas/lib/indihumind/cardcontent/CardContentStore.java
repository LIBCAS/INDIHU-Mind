package cz.cas.lib.indihumind.cardcontent;

import core.store.DatedStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardContentStore extends DatedStore<CardContent, QCardContent> {
    public CardContentStore() {
        super(CardContent.class, QCardContent.class);
    }

    public CardContent findLastVersionOfCard(String cardId) {
        CardContent cardContent = query()
                .select(qObject())
                .where(qObject().card.id.eq(cardId))
                .where(qObject().lastVersion.isTrue())
                .where(qObject().deleted.isNull())
                .fetchFirst();
        detachAll();
        return cardContent;
    }

    public List<CardContent> findCardContentsForCard(String cardId) {
        List<CardContent> cardContents = query()
                .select(qObject())
                .where(qObject().card.id.eq(cardId))
                .orderBy(qObject().created.desc())
                .where(qObject().deleted.isNull())
                .fetch();
        detachAll();
        return cardContents;
    }

}
