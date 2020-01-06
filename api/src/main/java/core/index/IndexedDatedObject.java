package core.index;

import core.domain.DatedObject;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;

import java.util.Date;

/**
 * Building block for Indexed entities, which want to track creation and update.
 * <p>
 * Provides attributes {@link IndexedDatedObject#created} and {@link IndexedDatedObject#updated}.
 * </p>
 * <p>
 * Unlike {@link DatedObject} there is no deleted attribute. Deleted entites are always removed from Indexed.
 * Also unlike {@link DatedObject} the attributes are of {@link Date} type, because Solr does not
 * understand Java 8 Time classes.
 * </p>
 */

@Getter
@Setter
public abstract class IndexedDatedObject extends IndexedDomainObject {

    @Field
    @Indexed(type = IndexFieldType.DATE)
    protected Date created;

    @Field
    @Indexed(type = IndexFieldType.DATE)
    protected Date updated;
}
