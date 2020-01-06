package cz.cas.lib.vzb.reference.marc;

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
public class IndexedRecord extends IndexedNamedObject {

    public static final String USER_ID = "user_id";
    public static final String LEADER = "leader";
    public static final String DATAFIELD_IDS = "datafield_ids";

    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = LEADER)
    @Indexed(type = IndexFieldType.STRING)
    private String leader;

    @Field(value = DATAFIELD_IDS)
    @Indexed(type = IndexFieldType.STRING)
    private List<String> dataFields;

}
