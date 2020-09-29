package cz.cas.lib.vzb.search.service;

import core.index.dto.Result;
import cz.cas.lib.vzb.search.query.IndexedQueryField;
import cz.cas.lib.vzb.search.query.Query;
import cz.cas.lib.vzb.search.query.QueryDto;
import cz.cas.lib.vzb.search.searchable.AdvancedSearch;
import cz.cas.lib.vzb.search.searchable.AdvancedSearchClass;
import cz.cas.lib.vzb.search.searchable.AdvancedSearchStore;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface AdvancedSearchService {

    /**
     * Returns available classes and their fields for advanced search.
     *
     * Indexed classes and their stores must implement {@link AdvancedSearchClass} and {@link AdvancedSearchStore}.
     * Fields of indexed classes must be annotated with {@link AdvancedSearch}.
     *
     * @return map of {indexedClassName : [Field1, Field2, ...]}
     */
    Map<String, Set<IndexedQueryField>> getFieldsOfAdvancedSearch();

    /**
     * Performs query against Solr and returns a result with entities.
     *
     * @param dto containing name of class and params
     * @param <U> type of indexed entity that is returned; the type is obtained from indexed class name
     * @return result with queried entities, only ID and name are filled
     */
    <U extends AdvancedSearchClass> Result<U> searchWithClass(QueryDto dto);


    /**
     * Create or update query entity.
     *
     * @param entity query
     * @return saved entity with JPA generated values filled
     */
    Query save(Query entity);


    /**
     * Delete query entity from persistent storage.
     *
     * @param id of entity
     */
    void hardDelete(String id);

    /**
     * Retrieve query entity with given id.
     *
     * @param id of sought entity
     * @return entity
     */
    Query findQuery(String id);

    /**
     * Retrieve all query entities of logged in user.
     *
     * @return list of entities
     */
    Collection<Query> findByUser();

}
