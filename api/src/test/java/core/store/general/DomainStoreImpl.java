package core.store.general;


import core.store.DomainStore;

import java.util.List;

public class DomainStoreImpl extends DomainStore<GeneralTestEntity, QGeneralTestEntity> {

    public DomainStoreImpl() {
        super(GeneralTestEntity.class, QGeneralTestEntity.class);
    }

    public List<GeneralTestEntity> findByRelation(String relatedObjectId) {
        List<GeneralTestEntity> relatedObjects = query().select(qObject()).where(propertyObjectPath("relatedObject").eq(relatedObjectId)).fetch();
        detachAll();
        return relatedObjects;
    }
}
