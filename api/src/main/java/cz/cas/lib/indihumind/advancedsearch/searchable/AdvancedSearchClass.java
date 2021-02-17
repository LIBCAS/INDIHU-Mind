package cz.cas.lib.indihumind.advancedsearch.searchable;

import cz.cas.lib.indihumind.card.IndexedCard;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * Interface that must be implemented by indexed classes participating in advanced search.
 *
 * Its methods are used in creating pre-filters for query.
 */
public interface AdvancedSearchClass {

    /**
     * Return name of Solr field that represents entity's owner ID (e.g. {@link IndexedCard#getUserId()})
     *
     * This field is used in pre-filter, query must return entities of user that requested them.
     *
     * @return name of solr field
     */
    @NotNull
    String getUserIdField();

    /**
     * Return name of Solr field that is represents deletion of entity. (e.g. {@link IndexedCard#getDeleted()})
     *
     * This field is used in pre-filter, query must not return deleted entities.
     *
     * @return name of solr field
     */
    @Nullable
    String getDeletedField();

}
