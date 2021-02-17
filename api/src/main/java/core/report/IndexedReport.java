package core.report;

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

/**
 * Indexed representation of {@link ReportTemplate}.
 */
@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedReport extends IndexedDatedObject {

    public static final String USER_ID = "user_id";
    public static final String NAME = "name";
    public static final String FILE_NAME = "file_name";

    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = NAME)
    @Indexed(type = IndexFieldType.KEYWORD, copyTo = {NAME + IndexField.SORT_SUFFIX})
    @AdvancedSearch(czech = "Název", type = QueryType.STRING)
    protected String name;

    @Field(value = FILE_NAME)
    @Indexed(type = IndexFieldType.KEYWORD, copyTo = {NAME + IndexField.SORT_SUFFIX})
    protected String fileName;

}
