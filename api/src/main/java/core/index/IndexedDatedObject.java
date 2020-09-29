package core.index;

import core.domain.DatedObject;
import cz.cas.lib.vzb.search.searchable.AdvancedSearch;
import cz.cas.lib.vzb.search.query.QueryType;
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

    public static final String CREATED = "created";
    public static final String UPDATED = "updated";

    @Field(value = CREATED)
    @Indexed(type = IndexFieldType.DATE)
    @AdvancedSearch(czech = "Datum vytvoření", type = QueryType.DATE)
    protected Date created;

    @Field(value = UPDATED)
    @Indexed(type = IndexFieldType.DATE)
    @AdvancedSearch(czech = "Datum poslední modifikace", type = QueryType.DATE)
    protected Date updated;
}
