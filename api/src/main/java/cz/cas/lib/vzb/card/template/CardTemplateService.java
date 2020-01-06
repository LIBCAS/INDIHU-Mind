package cz.cas.lib.vzb.card.template;

import core.exception.ForbiddenObject;
import core.rest.data.DelegateAdapter;
import cz.cas.lib.vzb.card.attribute.AttributeTemplate;
import cz.cas.lib.vzb.card.attribute.AttributeTemplateStore;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static core.util.Utils.eq;

@Service
public class CardTemplateService implements DelegateAdapter<CardTemplate> {
    @Getter
    private CardTemplateStore delegate;
    private AttributeTemplateStore attributeTemplateStore;
    private UserDelegate userDelegate;

    public List<CardTemplate> findTemplates(String userId) {
        return delegate.findTemplates(userId);
    }

    @Override
    public CardTemplate find(String id) {
        return delegate.findAndFill(id);
    }

    @Override
    public CardTemplate save(CardTemplate newTemplate) {
        newTemplate.setOwner(userDelegate.getUser());
        CardTemplate templateInDb = find(newTemplate.getId());
        if (templateInDb != null) {
            eq(templateInDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(CardTemplate.class, newTemplate.getId()));
            templateInDb.getAttributeTemplates().stream().filter(a -> !newTemplate.getAttributeTemplates().contains(a)).forEach(
                    a -> attributeTemplateStore.hardDelete(a)
            );
        }
        Set<AttributeTemplate> attributeTemplates = newTemplate.getAttributeTemplates();
        newTemplate.setAttributeTemplates(null);
        delegate.save(newTemplate);
        attributeTemplates.forEach(a -> a.setCardTemplate(newTemplate));
        attributeTemplateStore.save(attributeTemplates);
        newTemplate.setAttributeTemplates(attributeTemplates);
        return newTemplate;
    }

    @Override
    public Collection<? extends CardTemplate> save(Collection<? extends CardTemplate> entities) {
        throw new UnsupportedOperationException();
    }

    @Inject
    public void setDelegate(CardTemplateStore delegate) {
        this.delegate = delegate;
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
