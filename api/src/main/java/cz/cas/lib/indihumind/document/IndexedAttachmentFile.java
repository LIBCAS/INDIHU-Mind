package cz.cas.lib.indihumind.document;

import core.index.IndexField;
import core.index.IndexFieldType;
import core.index.IndexedDatedObject;
import cz.cas.lib.indihumind.advancedsearch.query.QueryType;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearch;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;


@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedAttachmentFile extends IndexedDatedObject {

    public static final String USER_ID = "user_id";
    public static final String PROVIDER_TYPE = "provider_type";
    public static final String NAME = "name";


    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = PROVIDER_TYPE)
    @Indexed(type = IndexFieldType.STRING)
    private String providerType;

    @Field(value = NAME)
    @Indexed(type = IndexFieldType.KEYWORD, copyTo = {NAME + IndexField.SORT_SUFFIX})
    @AdvancedSearch(czech = "Název", type = QueryType.STRING)
    protected String name;

}
