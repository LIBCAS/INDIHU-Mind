package core.notification;

import core.index.IndexFieldType;
import core.index.IndexedDatedObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Indexed representation of {@link Notification}.
 */
@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedNotification extends IndexedDatedObject {

    @Indexed(type = IndexFieldType.KEYWORD)
    @Field
    protected String title;

    @Indexed(type = IndexFieldType.KEYWORD)
    @Field
    protected String authorName;

    @Indexed(type = IndexFieldType.STRING)
    @Field
    protected String authorId;

    @Indexed(type = IndexFieldType.KEYWORD)
    @Field
    protected String recipientName;

    @Indexed(type = IndexFieldType.STRING)
    @Field
    protected String recipientId;

    @Indexed(type = IndexFieldType.BOOLEAN)
    @Field
    protected Boolean flash;

    @Indexed(type = IndexFieldType.BOOLEAN)
    @Field
    protected Boolean read;

    @Indexed(type = IndexFieldType.BOOLEAN)
    @Field
    protected Boolean emailing;
}
