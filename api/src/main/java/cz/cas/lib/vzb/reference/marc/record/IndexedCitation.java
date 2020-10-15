package cz.cas.lib.vzb.reference.marc.record;

import core.index.IndexFieldType;
import core.index.IndexedNamedObject;
import cz.cas.lib.vzb.search.searchable.AdvancedSearchClass;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedCitation extends IndexedNamedObject implements AdvancedSearchClass {

    public static final String USER_ID = "user_id";

    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;


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
