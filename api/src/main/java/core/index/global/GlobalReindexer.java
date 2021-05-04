package core.index.global;

import core.Changed;
import core.index.IndexedStore;
import core.store.Transactional;
import core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@Service
@Deprecated
@Changed("Use StoreReindexer class and Reindexable interface")
public class GlobalReindexer {
    private List<IndexedStore> stores;

    /**
     * Reindexes all detected IndexedStores
     */
    @Transactional
    public void reindex() {
        reindexSubset(true, null);
    }

    /**
     * Reindexes only specified IndexedStores or all if not specified
     *
     * @param dropIndexes  specify if indexes should be dropped prior to reindexing
     * @param storeClasses storeAip classes subset to reindex or null if all
     */
    @Transactional
    public void reindexSubset(boolean dropIndexes, List<Class<? extends IndexedStore>> storeClasses) {
        if (dropIndexes) stores.stream()
                .map(Utils::unwrap)
                .filter(store -> storeClasses == null || storeClasses.contains(store.getClass()))
                .forEach(store -> {
                    log.info("Reindexing entity:'{}' for store:'{}'...", store.getUType().getSimpleName(), store.getClass().getName());
                    store.dropReindex();
                    log.info("Reindexing for store:'{}' complete", store.getClass().getName());
                });
        else
            stores.stream()
                    .map(Utils::unwrap)
                    .filter(store -> storeClasses == null || storeClasses.contains(store.getClass()))
                    .forEach(store -> {
                        log.info("Reindexing entity:'{}' for store:'{}'...", store.getUType().getSimpleName(), store.getClass().getName());
                        store.reindex();
                        log.info("Reindexing for store:'{}' complete", store.getClass().getName());
                    });
    }

    @Transactional
    public void removeIndexes(List<Class<? extends IndexedStore>> storeClasses) {
         stores.stream()
                .map(Utils::unwrap)
                .filter(store -> storeClasses == null || storeClasses.contains(store.getClass()))
                .forEach(store -> {
                    log.info("Removing indexes for entity:'{}', store {}", store.getUType().getSimpleName(), store.getClass().getName());
                    store.removeAllIndexes();
                    log.info("Removal of indexes is complete.");
                });
    }

    @Inject
    public void setStores(List<IndexedStore> stores) {
        this.stores = stores;
    }
}
