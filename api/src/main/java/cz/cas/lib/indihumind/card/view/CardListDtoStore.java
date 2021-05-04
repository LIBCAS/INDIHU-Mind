package cz.cas.lib.indihumind.card.view;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.card.IndexedCard;
import cz.cas.lib.indihumind.card.CardStore;
import org.springframework.stereotype.Repository;

@Repository
public class CardListDtoStore extends IndexedDatedStore<CardListDto, QCardListDto, IndexedCard> {

    public CardListDtoStore() {
        super(CardListDto.class, QCardListDto.class, IndexedCard.class);
    }

    @Override
    public String getIndexType() {
        return CardStore.INDEX_TYPE;
    }

}
