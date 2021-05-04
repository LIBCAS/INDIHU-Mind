package cz.cas.lib.indihumind.card.view;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.card.IndexedCard;
import cz.cas.lib.indihumind.card.CardStore;
import org.springframework.stereotype.Repository;


@Repository
public class CardRefStore extends IndexedDatedStore<CardRef, QCardRef, IndexedCard> {

    public CardRefStore() {
        super(CardRef.class, QCardRef.class, IndexedCard.class);
    }

    @Override
    public String getIndexType() {
        return CardStore.INDEX_TYPE;
    }

}
