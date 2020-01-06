package cz.cas.lib.vzb.card.attribute;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class AttributeTemplateStore extends DomainStore<AttributeTemplate, QAttributeTemplate> {
    public AttributeTemplateStore() {
        super(AttributeTemplate.class, QAttributeTemplate.class);
    }

}
