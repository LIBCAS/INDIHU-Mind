package cz.cas.lib.indihumind.util.projection;

import core.domain.DomainObject;

/**
 * Interface representing a hibernate projection class for an entity
 * @param <ENTITY> type to link a projection with its main entity
 */
public interface EntityProjection<ENTITY extends DomainObject> {

    /**
     * Converts this instance to appropriate entity instance.
     * This method should set all available properties on resulting instance.
     */
    ENTITY toEntity();
}
