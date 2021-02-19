package cz.cas.lib.indihumind.advancedsearch.query;

import com.fasterxml.jackson.databind.util.StdConverter;


/**
 * Custom JSON converter for QueryFilter
 *
 * Converter ensures {@link QueryFilter#getValue()} is ready to be used in Solr Query by calling {@link
 * QueryType#formatQueryValue} on {@link QueryFilter#getType}. This way if any change of value is needed for solr query
 * or additional types are introduced then all change can happen in single place: {@link QueryType}.
 */
public class QueryFilterConverter extends StdConverter<QueryFilter, QueryFilter> {

    @Override
    public QueryFilter convert(QueryFilter filterWithRawValue) {
        // creates string ready for solr query
        String solrQueryValue = filterWithRawValue.getType().formatQueryValue(filterWithRawValue.getValue());
        filterWithRawValue.setValue(solrQueryValue);
        return filterWithRawValue;
    }
}
