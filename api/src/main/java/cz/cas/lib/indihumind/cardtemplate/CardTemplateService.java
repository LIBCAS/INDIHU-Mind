package cz.cas.lib.indihumind.cardtemplate;

import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.store.Transactional;
import cz.cas.lib.indihumind.cardattribute.AttributeTemplate;
import cz.cas.lib.indihumind.cardattribute.AttributeTemplateStore;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_FAILED_COMPARISON;
import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.eq;
import static core.util.Utils.notNull;

@Service
public class CardTemplateService {

    private CardTemplateStore store;
    private AttributeTemplateStore attributeTemplateStore;
    private UserDelegate userDelegate;

    public List<CardTemplate> findTemplates(String userId) {
        return store.findTemplates(userId);
    }

    public CardTemplate find(String id) {
        return store.findAndFill(id);
    }

    @Transactional
    public void delete(String id) {
        CardTemplate entity = find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, CardTemplate.class, id));
        notNull(entity.getOwner(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, CardTemplate.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, CardTemplate.class, id));
        store.delete(entity);
    }

    @Transactional
    public CardTemplate save(String id, CardTemplate newTemplate) {
        eq(id, newTemplate.getId(), () -> new BadArgument(ARGUMENT_FAILED_COMPARISON, "id != dto.id"));

        newTemplate.setOwner(userDelegate.getUser());
        CardTemplate templateInDb = find(newTemplate.getId());
        if (templateInDb != null) {
            eq(templateInDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, CardTemplate.class, newTemplate.getId()));
            templateInDb.getAttributeTemplates().stream().filter(a -> !newTemplate.getAttributeTemplates().contains(a)).forEach(
                    a -> attributeTemplateStore.hardDelete(a)
            );
        }
        Set<AttributeTemplate> attributeTemplates = newTemplate.getAttributeTemplates();
        newTemplate.setAttributeTemplates(null);
        store.save(newTemplate);
        attributeTemplates.forEach(a -> a.setCardTemplate(newTemplate));
        attributeTemplateStore.save(attributeTemplates);
        newTemplate.setAttributeTemplates(attributeTemplates);
        return newTemplate;
    }

    public Collection<CardTemplate> findByUser(String id) {
        return store.findByUser(id);
    }


    @Inject
    public void setStore(CardTemplateStore store) {
        this.store = store;
    }

    @Inject
    public void setAttributeTemplateStore(AttributeTemplateStore attributeTemplateStore) {
        this.attributeTemplateStore = attributeTemplateStore;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

}
