package cz.cas.lib.indihumind.util;

/**
 * Common interface for manual reindexing.
 *
 * Reindexing cannot be done with IndexedDomainStores because
 * entities have projections and those have IndexedDomainStores as well.
 */
public interface Reindexable {
    void reindexEverything();

    void removeAllDataFromIndex();
}
