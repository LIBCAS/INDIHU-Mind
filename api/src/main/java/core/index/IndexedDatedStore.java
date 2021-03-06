package core.index;

import com.querydsl.core.types.dsl.EntityPathBase;
import core.domain.DatedObject;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.DatedStore;
import core.store.Transactional;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.data.solr.core.SolrTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;

import static core.util.Utils.toDate;

@Getter
@Transactional
public abstract class IndexedDatedStore<T extends DatedObject, Q extends EntityPathBase<T>, U extends IndexedDatedObject>
        extends DatedStore<T, Q> implements IndexedStore<T, U> {
    protected SolrTemplate template;

    private Class<U> uType;

    public IndexedDatedStore(Class<T> type, Class<Q> qType, Class<U> uType) {
        super(type, qType);
        this.uType = uType;
    }

    /**
     * Converts a JPA instance to an Solr instance.
     * <p>
     * <p>
     * Subclasses should call super to reuse the provided mapping for {@link DatedObject}
     * </p>
     *
     * @param obj JPA instance
     * @return Solr instance
     */
    @SneakyThrows
    public U toIndexObject(T obj) {
        U u = getIndexObjectInstance();
        u.setId(obj.getId());
        u.setType(getIndexType());
        u.setCreated(toDate(obj.getCreated()));
        u.setUpdated(toDate(obj.getUpdated()));
        return u;
    }

    @Override
    public T save(T entity) {
        entity = super.save(entity);
        return IndexedStore.super.save(entity);
    }

    @Override
    public Collection<? extends T> save(Collection<? extends T> entities) {
        entities = super.save(entities);
        return IndexedStore.super.save(entities);
    }

    @Override
    public void delete(T entity) {
        super.delete(entity);
        IndexedStore.super.delete(entity);
    }

    @Override
    public Result<T> findAll(Params params) {
        return IndexedStore.super.findAll(params);
    }

    @Inject
    public void setTemplate(SolrTemplate template) {
        this.template = template;
    }


    @Override
    @PostConstruct
    public void init() {
        analyzeIndexedClass();
    }
}
