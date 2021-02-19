package cz.cas.lib.indihumind.citationtemplate;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchStore;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Indexed repository for indexed {@link IndexedReferenceTemplate} and class {@link ReferenceTemplate}
 */
@Repository
public class ReferenceTemplateStore extends IndexedDatedStore<ReferenceTemplate, QReferenceTemplate, IndexedReferenceTemplate> implements AdvancedSearchStore<IndexedReferenceTemplate> {

    public ReferenceTemplateStore() {
        super(ReferenceTemplate.class, QReferenceTemplate.class, IndexedReferenceTemplate.class);
    }

    public final String indexType = "template";

    @Override
    public String getIndexType() {
        return indexType;
    }

    /**
     * For constraint check; Must be unique name for owner
     **/
    public ReferenceTemplate findEqualNameDifferentId(ReferenceTemplate newTemplate) {
        if (requireNonNull(newTemplate).getName() == null) return null;

        ReferenceTemplate entity = query()
                .select(qObject())
                .where(qObject().owner.id.eq(newTemplate.getOwner().getId()))
                .where(qObject().deleted.isNull())
                .where(qObject().name.eq(newTemplate.getName()))
                .where(qObject().id.ne(newTemplate.getId()))
                .fetchFirst();
        detachAll();
        return entity;
    }

    @Override
    public IndexedReferenceTemplate toIndexObject(ReferenceTemplate template) {
        IndexedReferenceTemplate indexed = super.toIndexObject(template);
        if (template.getName() != null) indexed.setName(template.getName());
        if (template.getOwner() != null) indexed.setUserId(template.getOwner().getId());
        return indexed;
    }


    // ------------------------------------ ADVANCED SEARCH MIXIN ------------------------------------
    @Override
    public SolrTemplate getSolrTemplateForSearch() {
        return getTemplate();
    }

    @Override
    public Criteria getTypeCriteriaForSearch() {
        return typeCriteria();
    }

    @Override
    public Class<IndexedReferenceTemplate> getTypeClassForSearch() {
        return getUType();
    }

    @Override
    public Supplier<IndexedReferenceTemplate> getConstructor() {
        return IndexedReferenceTemplate::new;
    }

}
