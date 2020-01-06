package core.rest.data;

import core.domain.DomainObject;
import core.index.dto.Params;
import core.index.dto.Result;

import java.util.Collection;

public interface DelegateAdapter<T extends DomainObject> extends DataAdapter<T> {
    DataAdapter<T> getDelegate();

    @Override
    default Class<T> getType() {
        return getDelegate().getType();
    }

    @Override
    default T find(String id) {
        return getDelegate().find(id);
    }

    @Override
    default Result<T> findAll(Params params) {
        return getDelegate().findAll(params);
    }

    @Override
    default T save(T entity) {
        return getDelegate().save(entity);
    }

    @Override
    default Collection<? extends T> save(Collection<? extends T> entities) {
        return getDelegate().save(entities);
    }

    @Override
    default void delete(T entity) {
        getDelegate().delete(entity);
    }

    @Override
    default void hardDelete(T entity) {
        getDelegate().hardDelete(entity);
    }

    @Override
    default Collection<T> findByUser(String ownerId) {return getDelegate().findByUser(ownerId);}

    default boolean isOwner(String recordId, String userId) {return getDelegate().isOwner(recordId, userId);}
}
