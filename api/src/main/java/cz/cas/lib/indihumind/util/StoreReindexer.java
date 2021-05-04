package cz.cas.lib.indihumind.util;

import core.store.Transactional;
import core.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class StoreReindexer {

    private List<Reindexable> reidexableStores;

    @Transactional
    public void reindexData() {
        reidexableStores.stream()
                .map(Utils::unwrap)
                .filter(Objects::nonNull)
                .forEach(store -> {
                    log.info("Reindex of store:'{}' begins...", store.getClass().getName());
                    store.reindexEverything();
                    log.info("Reindex of store:'{}' is completed", store.getClass().getName());
                });
    }

    @Transactional
    public void removeIndexes() {
        reidexableStores.stream()
                .map(Utils::unwrap)
                .filter(Objects::nonNull)
                .forEach(store -> {
                    log.info("Index removal of store:'{}' begins...", store.getClass().getName());
                    store.removeAllDataFromIndex();
                    log.info("Index removal of store:'{}' is completed", store.getClass().getName());
                });
    }

    @Inject
    public void setReidexableStores(List<Reindexable> reidexableStores) {
        this.reidexableStores = reidexableStores;
    }
}
