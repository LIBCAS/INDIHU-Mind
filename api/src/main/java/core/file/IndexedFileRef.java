package core.file;

import core.index.IndexFieldType;
import core.index.IndexedDatedObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Indexed representation of {@link FileRef}.
 */
@Getter
@Setter
@SolrDocument(collection = "uas")
public class IndexedFileRef extends IndexedDatedObject {

    @Field
    @Indexed(type = IndexFieldType.FOLDING)
    protected String name;

    @Field
    @Indexed(type = IndexFieldType.STRING)
    protected String content;
}
