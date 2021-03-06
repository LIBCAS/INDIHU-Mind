package core.index;

import com.querydsl.core.types.dsl.EntityPathBase;
import core.domain.NamedObject;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.NamedStore;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.data.solr.core.SolrTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Collection;

import static core.util.Utils.toDate;

@Getter
public abstract class IndexedNamedStore<T extends NamedObject, Q extends EntityPathBase<T>, U extends IndexedNamedObject>
        extends NamedStore<T, Q> implements IndexedStore<T, U> {
    private SolrTemplate template;

    private Class<U> uType;

    public IndexedNamedStore(Class<T> type, Class<Q> qType, Class<U> uType) {
        super(type, qType);
        this.uType = uType;
    }

    @SneakyThrows
    public U toIndexObject(T obj) {
        U u = getIndexObjectInstance();
        u.setId(obj.getId());
        u.setType(getIndexType());
        u.setCreated(toDate(obj.getCreated()));
        u.setUpdated(toDate(obj.getUpdated()));
        u.setName(obj.getName());
        return u;
    }

    @Override
    public T save(T entity) {
        entity = super.save(entity);
        return IndexedStore.super.save(entity);
    }

    @Override
    public Collection<? extends T> save(Collection<? extends T> entities) {
        super.save(entities);
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