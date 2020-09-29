package cz.cas.lib.vzb.reference.marc.record;

import core.store.NamedStore;
import org.springframework.stereotype.Repository;

@Repository
public class MarcRecordStore extends NamedStore<MarcRecord, QMarcRecord> {
    public MarcRecordStore() { super(MarcRecord.class, QMarcRecord.class); }
}
