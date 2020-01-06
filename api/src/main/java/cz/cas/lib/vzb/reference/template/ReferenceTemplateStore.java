package cz.cas.lib.vzb.reference.template;

import core.index.IndexedNamedStore;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Indexed repository for indexed {@link IndexedReferenceTemplate} and class {@link ReferenceTemplate}
 */
@Repository
public class ReferenceTemplateStore extends IndexedNamedStore<ReferenceTemplate, QReferenceTemplate, IndexedReferenceTemplate> {
    public ReferenceTemplateStore() {
        super(ReferenceTemplate.class, QReferenceTemplate.class, IndexedReferenceTemplate.class);
    }

    @Getter
    public final String indexType = "template";


    /**
     * For constraint check; Must be unique name for owner
     **/
    public ReferenceTemplate findEqualNameDifferentId(ReferenceTemplate newTemplate) {
        if (requireNonNull(newTemplate).getName() == null) return null;

        ReferenceTemplate entity = query()
                .select(qObject())
                .where(qObject().name.eq(newTemplate.getName()))
                .where(qObject().id.ne(newTemplate.getId()))
                .fetchFirst();
        detachAll();
        return entity;
    }


    @Override
    public IndexedReferenceTemplate toIndexObject(ReferenceTemplate template) {
        IndexedReferenceTemplate indexed = super.toIndexObject(template);
        indexed.setUserId(template.getOwner().getId());
        indexed.setPattern(template.getPattern());
        indexed.setCustomizedFields(template.getFields()
                .stream()
                .map(CustomizedField::getId)
                .collect(Collectors.toList()));
        return indexed;
    }
}
