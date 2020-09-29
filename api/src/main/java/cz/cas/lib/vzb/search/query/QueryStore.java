package cz.cas.lib.vzb.search.query;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;


@Repository
public class QueryStore extends DomainStore<Query, QQuery> {

    public QueryStore() {
        super(Query.class, QQuery.class);
    }

}
