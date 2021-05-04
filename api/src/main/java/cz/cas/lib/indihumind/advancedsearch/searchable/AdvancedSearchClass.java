package cz.cas.lib.indihumind.advancedsearch.searchable;

import cz.cas.lib.indihumind.citation.IndexedCitation;

import javax.validation.constraints.NotNull;

/**
 * Interface that must be implemented by indexed classes participating in advanced search.
 *
 * Its methods are used in creating pre-filters for query.
 */
public interface AdvancedSearchClass {

    /**
     * Return name of Solr field that represents entity's owner ID (e.g. {@link IndexedCitation#getUserId()})
     *
     * This field is used in pre-filter, query must return entities of user that requested them.
     *
     * @return name of solr field
     */
    @NotNull
    String getUserIdField();

}
