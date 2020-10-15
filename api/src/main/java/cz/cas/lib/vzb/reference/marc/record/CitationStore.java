package cz.cas.lib.vzb.reference.marc.record;

import core.index.IndexedNamedStore;
import cz.cas.lib.vzb.search.searchable.AdvancedSearchStore;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Indexed repository for indexed {@link IndexedCitation} and class {@link Citation}
 */
@Repository
public class CitationStore extends IndexedNamedStore<Citation, QCitation, IndexedCitation> implements AdvancedSearchStore<IndexedCitation> {

    public CitationStore() {
        super(Citation.class, QCitation.class, IndexedCitation.class);
    }

    public final String indexType = "citation";

    @Override
    public String getIndexType() {
        return indexType;
    }

    /**
     * For constraint check; Must be unique name for owner
     */
    public Citation findEqualNameDifferentId(Citation newCitation) {
        if (requireNonNull(newCitation).getName() == null) return null;

        Citation entity = query()
                .select(qObject())
                .where(qObject().owner.id.eq(newCitation.getOwner().getId()))
                .where(qObject().deleted.isNull())
                .where(qObject().name.eq(newCitation.getName()))
                .where(qObject().id.ne(newCitation.getId()))
                .fetchFirst();
        detachAll();
        return entity;
    }


    @Override
    public IndexedCitation toIndexObject(Citation obj) {
        IndexedCitation indexedCitation = super.toIndexObject(obj);
        if (obj.getOwner() != null) indexedCitation.setUserId(obj.getOwner().getId());
        return indexedCitation;
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
    public Class<IndexedCitation> getTypeClassForSearch() {
        return getUType();
    }

    @Override
    public Supplier<IndexedCitation> getConstructor() {
        return IndexedCitation::new;
    }

}
