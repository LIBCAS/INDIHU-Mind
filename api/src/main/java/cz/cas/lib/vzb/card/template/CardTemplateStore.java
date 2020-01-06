package cz.cas.lib.vzb.card.template;

import com.querydsl.jpa.impl.JPAQuery;
import core.store.NamedStore;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardTemplateStore extends NamedStore<CardTemplate, QCardTemplate> {
    public CardTemplateStore() {
        super(CardTemplate.class, QCardTemplate.class);
    }

    /**
     * returns all common (shared) templates and, if userId is specified also templates of the user
     *
     * @param userId
     * @return
     */
    public List<CardTemplate> findTemplates(String userId) {
        JPAQuery<CardTemplate> query = query()
                .select(qObject())
                .where(qObject().deleted.isNull());
        if (userId == null)
            query.where(qObject().owner.isNull());
        else
            query.where(qObject().owner.isNull().or(qObject().owner.id.eq(userId)));
        List<CardTemplate> fetch = query.fetch();
        detachAll();
        return fetch;
    }

    public CardTemplate findAndFill(String id) {
        CardTemplate cardTemplate = query()
                .where(qObject().deleted.isNull())
                .select(qObject())
                .leftJoin(qObject().attributeTemplates)
                .fetchJoin()
                .where(qObject().id.eq(id))
                .fetchFirst();
        detachAll();
        return cardTemplate;
    }

}
