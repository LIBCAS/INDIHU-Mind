package core.index.nested;

import core.index.IndexFieldType;
import core.index.IndexedDomainObject;
import core.index.IndexField;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@Getter
@Setter
@SolrDocument(collection = "uas-test")
public class IndexedParentEntity extends IndexedDomainObject {
    @Field
    @Indexed(type = IndexFieldType.TEXT, copyTo = {"attribute" + IndexField.STRING_SUFFIX, "attribute" + IndexField.SORT_SUFFIX})
    private String attribute;
}
