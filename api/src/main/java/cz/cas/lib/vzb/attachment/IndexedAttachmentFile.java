package cz.cas.lib.vzb.attachment;

import core.index.IndexFieldType;
import core.index.IndexedNamedObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;


@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedAttachmentFile extends IndexedNamedObject {

    public static final String USER_ID = "user_id";
    public static final String PROVIDER_TYPE = "provider_type";


    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = PROVIDER_TYPE)
    @Indexed(type = IndexFieldType.STRING)
    private String providerType;

}
