package cz.cas.lib.indihumind.advancedsearch.searchable;

import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.SimpleFilterQuery;

import java.util.function.Supplier;

/**
 * Stores of indexed entities that participate in advanced search must implement this interface.
 *
 * @param <T> indexed class which belongs to the particular store.
 */
public interface AdvancedSearchStore<T extends AdvancedSearchClass> {

    SolrTemplate getSolrTemplateForSearch();

    Criteria getTypeCriteriaForSearch();

    Class<T> getTypeClassForSearch();

    Supplier<T> getConstructor();


    default FilterQuery createPrefilter(UserDelegate userDelegate) {
        T instance = getConstructor().get();

        FilterQuery fq = new SimpleFilterQuery();
        fq.addCriteria(getTypeCriteriaForSearch());

        if (instance.getUserIdField() != null) // filterOwner
            fq.addCriteria(Criteria.where(instance.getUserIdField()).is(userDelegate.getId()));

        return fq;
    }

}
