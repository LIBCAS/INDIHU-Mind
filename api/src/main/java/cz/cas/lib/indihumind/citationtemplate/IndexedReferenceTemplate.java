package cz.cas.lib.indihumind.citationtemplate;

import core.index.IndexField;
import core.index.IndexFieldType;
import core.index.IndexedDatedObject;
import cz.cas.lib.indihumind.advancedsearch.query.QueryType;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearch;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchClass;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedReferenceTemplate extends IndexedDatedObject implements AdvancedSearchClass {

    public static final String USER_ID = "user_id";
    public static final String NAME = "name";

    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = NAME)
    @Indexed(type = IndexFieldType.KEYWORD, copyTo = {NAME + IndexField.SORT_SUFFIX})
    @AdvancedSearch(czech = "Název", type = QueryType.STRING)
    protected String name;

    // ------------------------------------ ADVANCED SEARCH CLASS ------------------------------------
    @Override
    public String getUserIdField() {
        return USER_ID;
    }

    @Override
    public String getDeletedField() {
        return null;
    }

}