package core.rest.data;


import core.domain.DomainObject;
import core.index.dto.Params;
import core.index.dto.Result;

import java.util.Collection;

public interface DataAdapter<T extends DomainObject> {
    Class<T> getType();

    T find(String id);

    Result<T> findAll(Params params);

    T save(T entity);

    Collection<? extends T> save(Collection<? extends T> entities);

    void delete(T entity);

    void hardDelete(T entity);

    Collection<T> findByUser(String ownerId);

    boolean isOwner(String recordId, String userId);
}
