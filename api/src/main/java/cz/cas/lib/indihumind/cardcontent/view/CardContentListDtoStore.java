package cz.cas.lib.indihumind.cardcontent.view;

import core.store.DatedStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardContentListDtoStore extends DatedStore<CardContentListDto, QCardContentListDto> {

    public CardContentListDtoStore() {
        super(CardContentListDto.class, QCardContentListDto.class);
    }

    public CardContentListDto findLastVersionOfCard(String cardId) {
        CardContentListDto cardContent = query()
                .select(qObject())
                .where(qObject().card.id.eq(cardId))
                .where(qObject().lastVersion.isTrue())
                .where(qObject().deleted.isNull())
                .fetchFirst();
        detachAll();
        return cardContent;
    }

    public List<CardContentListDto> listContentsForCard(String cardId) {
        List<CardContentListDto> cardContents = query()
                .select(qObject())
                .where(qObject().card.id.eq(cardId))
                .where(qObject().deleted.isNull())
                .orderBy(qObject().created.desc())
                .fetch();
        detachAll();
        return cardContents;
    }

}
