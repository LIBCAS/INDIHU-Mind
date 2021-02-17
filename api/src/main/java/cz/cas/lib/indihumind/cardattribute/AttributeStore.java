package cz.cas.lib.indihumind.cardattribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.store.DomainStore;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
public class AttributeStore extends DomainStore<Attribute, QAttribute> {
    private ObjectMapper objectMapper;

    public AttributeStore() {
        super(Attribute.class, QAttribute.class);
    }

    @Override
    public Attribute save(Attribute entity) {
        fillStringValue(Collections.singleton(entity));
        return super.save(entity);
    }

    @Override
    public Collection<? extends Attribute> save(Collection<? extends Attribute> entities) {
        fillStringValue(entities);
        return super.save(entities);
    }

    public Map<String, List<Attribute>> findStringAttributesOfLastContentOfCards(List<String> cardIds) {
        Map<String, List<Attribute>> transform = query()
                .select(qObject().cardContent.card.id, qObject())
                .where(qObject().cardContent.lastVersion.isTrue())
                .where(qObject().cardContent.card.id.in(cardIds))
                .where(qObject().type.eq(AttributeType.STRING))
                .orderBy(qObject().cardContent.created.desc())
                .transform(groupBy(qObject().cardContent.card.id).as(list(qObject())));
        detachAll();
        return transform;
    }

    private void fillStringValue(Collection<? extends Attribute> attributes) {
        try {
            for (Attribute attribute : attributes) {
                attribute.setJsonValue(objectMapper.writeValueAsString(attribute.getValue()));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
