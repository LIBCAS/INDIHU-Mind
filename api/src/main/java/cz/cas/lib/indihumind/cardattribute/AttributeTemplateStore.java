package cz.cas.lib.indihumind.cardattribute;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class AttributeTemplateStore extends DomainStore<AttributeTemplate, QAttributeTemplate> {
    public AttributeTemplateStore() {
        super(AttributeTemplate.class, QAttributeTemplate.class);
    }

}
