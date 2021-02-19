package cz.cas.lib.indihumind.advancedsearch.query;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;


@Repository
public class QueryStore extends DomainStore<Query, QQuery> {

    public QueryStore() {
        super(Query.class, QQuery.class);
    }

}
