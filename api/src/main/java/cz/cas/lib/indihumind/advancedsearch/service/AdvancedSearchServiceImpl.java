package cz.cas.lib.indihumind.advancedsearch.service;

import core.exception.ForbiddenObject;
import core.exception.GeneralException;
import core.exception.MissingObject;
import core.index.dto.Result;
import core.store.Transactional;
import core.util.Utils;
import cz.cas.lib.indihumind.advancedsearch.query.*;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearch;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchClass;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchStore;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.query.AnyCriteria;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.eq;
import static core.util.Utils.notNull;

@Service
@Slf4j
public class AdvancedSearchServiceImpl implements AdvancedSearchService {

    private Set<AdvancedSearchStore<? extends AdvancedSearchClass>> advancedSearchStores;
    private UserDelegate userDelegate;
    private QueryStore store;

    @Override
    public Map<String, Set<IndexedQueryField>> getFieldsOfAdvancedSearch() {
        Map<String, Set<IndexedQueryField>> solrSearchClasses = new HashMap<>();

        for (Map.Entry<String, Utils.Pair<Class<? extends AdvancedSearchClass>, String>> classEntry : AdvancedSearchLocator.SOLR_DOCUMENT_CLASSES.entrySet()) {
            Set<IndexedQueryField> solrSearchFields = findSearchFields(classEntry);
            if (!solrSearchFields.isEmpty()) // skip @SolrDocument classes with fields that have no @AdvancedSearch
                solrSearchClasses.put(classEntry.getKey(), solrSearchFields);
        }
        return solrSearchClasses;
    }

    @Override
    public <U extends AdvancedSearchClass> Result<U> searchWithClass(QueryDto dto) {
        Class<U> indexedClass = AdvancedSearchLocator.getClassFromName(dto.getIndexedClass());
        AdvancedSearchStore<U> indexedClassStore = getStoreForIndexedClass(indexedClass);

        SimpleQuery query = buildSimpleQuery(indexedClassStore, dto.getParams());

        SolrTemplate template = indexedClassStore.getSolrTemplateForSearch();
        String collectionName = AdvancedSearchLocator.getCollectionNameFor(indexedClass);
        ScoredPage<U> cardsPage = template.queryForPage(collectionName, query, indexedClassStore.getTypeClassForSearch());

        return Result.with(cardsPage.getContent(), cardsPage.getTotalElements());
    }

    // ------------------------- CRUD FOR QUERY ENTITY ----------------------------------
    @Override
    @Transactional
    public Query save(Query forSave) {
        Query fromDb = store.find(forSave.getId());

        if (fromDb == null) { // CREATE
            forSave.setOwner(userDelegate.getUser());
            log.debug(String.format("Creating new Query entity '%s' for user '%s'", forSave.getId(), userDelegate.getUser()));
        } else { // UPDATE
            eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Query.class, forSave.getId()));
            forSave.setOwner(userDelegate.getUser());
            log.debug(String.format("Updating Query entity '%s' of user '%s'", forSave.getId(), userDelegate.getUser()));
        }

        return store.save(forSave);
    }

    @Override
    @Transactional
    public void hardDelete(String id) {
        Query entity = findQuery(id);
        store.hardDelete(entity);
    }

    @Override
    public Query findQuery(String id) {
        Query entity = store.find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, Query.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Query.class, id));

        return entity;
    }

    @Override
    public Collection<Query> findByUser() {
        return store.findByUser(userDelegate.getId());
    }
    // ----------------------------------------------------------------------------------


    private <U extends AdvancedSearchClass> AdvancedSearchStore<U> getStoreForIndexedClass(Class<U> indexedClass) {
        for (AdvancedSearchStore<? extends AdvancedSearchClass> advancedSearchStore : advancedSearchStores) {
            if (advancedSearchStore.getTypeClassForSearch().equals(indexedClass)) {

                @SuppressWarnings("unchecked")
                AdvancedSearchStore<U> castedStore = (AdvancedSearchStore<U>) advancedSearchStore;

                return castedStore;
            }
        }

        throw new GeneralException("Can't find store class for class: " + indexedClass.getName());
    }

    private Set<IndexedQueryField> findSearchFields(Map.Entry<String, Utils.Pair<Class<? extends AdvancedSearchClass>, String>> classEntry) {
        Set<IndexedQueryField> solrSearchFields = new HashSet<>();
        for (java.lang.reflect.Field javaField : FieldUtils.getFieldsWithAnnotation(classEntry.getValue().getL(), AdvancedSearch.class)) {
            if (javaField.isAnnotationPresent(Dynamic.class))
                throw new GeneralException("Annotation @Dynamic cant be used with @AdvancedSearch. Define this field as @Indexed with specific type. Found in field: " + javaField.getName() + " of class: " + javaField.getClass());

            IndexedQueryField solrField = new IndexedQueryField(javaField);
            solrSearchFields.add(solrField);
        }
        return solrSearchFields;
    }

    private <U extends AdvancedSearchClass> SimpleQuery buildSimpleQuery(AdvancedSearchStore<U> indexedClassStore, QueryParams queryParams) {
        SimpleQuery query = new SimpleQuery();

        // q=*:*
        query.addCriteria(AnyCriteria.any());

        // prefilter USER_ID, not DELETED = deleted is null, INDEXTYPE
        query.addFilterQuery(indexedClassStore.createPrefilter(userDelegate));

        // sort by SCORE ASC
        query.addSort(Sort.by(Sort.Order.desc("score")));

        // use fl=id,name to return only ID, NAME
        query.addProjectionOnFields("id", "name");

        // paging
        if (queryParams.getPageSize() != null)
            query.setPageRequest(PageRequest.of(queryParams.getPageStart(), queryParams.getPageSize()));

        // creating filter queries (fq)
        for (QueryFilter filter : queryParams.getFilters()) {
            Criteria criteria = filter.getOperation().createQueryCriteria(filter.getField(), filter.getValue());
            query.addFilterQuery(new SimpleFilterQuery(criteria));
        }
        return query;
    }


    @Inject
    public void setAdvancedSearchStores(Set<AdvancedSearchStore<? extends AdvancedSearchClass>> advancedSearchStores) {
        this.advancedSearchStores = advancedSearchStores.stream()
                .map(Utils::unwrap)
                .collect(Collectors.toSet());
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setStore(QueryStore store) {
        this.store = store;
    }
}
