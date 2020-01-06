package cz.cas.lib.vzb.reference.template;

import core.index.IndexFieldType;
import core.index.IndexedNamedObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;

@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedReferenceTemplate extends IndexedNamedObject {

    public static final String USER_ID = "user_id";
    public static final String PATTERN = "pattern";
    public static final String CUSTOMIZED_FIELD_IDS = "customized_field_ids";

    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = PATTERN)
    @Indexed(type = IndexFieldType.STRING)
    private String pattern;

    @Field(value = CUSTOMIZED_FIELD_IDS)
    @Indexed(type = IndexFieldType.STRING)
    private List<String> customizedFields;

}