package core.index;

import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;


@Getter
@Setter
@SolrDocument(collection = "uas-test")
public class IndexedTestEntity extends IndexedDatedObject {

    @Field
    @Indexed(type = IndexFieldType.STRING, copyTo = {"sortableStringAttribute" + IndexField.SORT_SUFFIX})
    protected String customSortStringAttribute;

    @Field
    @Indexed(type = IndexFieldType.STRING)
    protected String stringAttribute;

    @Field
    @Indexed(type = IndexFieldType.TEXT)
    protected String textAttribute;

    @Field
    @Indexed(type = IndexFieldType.TEXT, copyTo = {"textAttributeWithStringCpyField" + IndexField.STRING_SUFFIX})
    protected String textAttributeWithStringCpyField;

    @Field
    @Indexed(type = IndexFieldType.KEYWORD)
    protected String foldingAttribute;

    @Field
    @Indexed(type = IndexFieldType.INT)
    private Integer intAttribute;

    @Field
    @Indexed(type = IndexFieldType.DOUBLE)
    private Double doubleAttribute;

    @Field
    @Indexed(type = IndexFieldType.DATE)
    private String localDateAttribute;

    @Field
    @Indexed(type = IndexFieldType.DATE)
    private String instantAttribute;
}
