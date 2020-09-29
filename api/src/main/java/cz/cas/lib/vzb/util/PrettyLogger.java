package cz.cas.lib.vzb.util;

import core.domain.DomainObject;

import java.util.Collection;
import java.util.stream.Collectors;

public class PrettyLogger {

    public static String collectionIds(Collection<? extends DomainObject> collection) {
        return String.format("[%s]", String.join(",", collection.stream()
                .map(DomainObject::getId)
                .map(id -> String.format("'%s'", id))
                .collect(Collectors.toSet())));
    }

}
