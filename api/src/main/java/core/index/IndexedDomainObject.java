package core.index;

import core.domain.DomainObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;

/**
 * Basic building block for every Solr entity.
 *
 * <p>
 * Defines attribute {@link IndexedDomainObject#id} of type {@link String}.
 * </p>
 * <p>
 * Always needs to have no arg constructor, otherwise exceptions will be thrown
 * in {@link IndexedStore#save(DomainObject)}.
 * </p>
 */
@NoArgsConstructor
@Getter
@Setter
public abstract class IndexedDomainObject {

    public static final String ID = "id";

    @Field(value = ID)
    @Indexed(type = IndexFieldType.STRING)
    protected String id;

    @Field(IndexQueryUtils.TYPE_FIELD)
    @Indexed(type = IndexFieldType.STRING)
    protected String type;

    public IndexedDomainObject(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndexedDomainObject that = (IndexedDomainObject) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}