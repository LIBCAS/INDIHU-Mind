package core.index;

import cz.cas.lib.vzb.search.query.QueryType;
import cz.cas.lib.vzb.search.searchable.AdvancedSearch;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;

@Getter
@Setter
public class IndexedNamedObject extends IndexedDatedObject {

    public static final String NAME = "name";

    @Field(value = NAME)
    @Indexed(type = IndexFieldType.KEYWORD, copyTo = {NAME + IndexField.SORT_SUFFIX})
    @AdvancedSearch(czech = "Název", type = QueryType.STRING)
    protected String name;

}