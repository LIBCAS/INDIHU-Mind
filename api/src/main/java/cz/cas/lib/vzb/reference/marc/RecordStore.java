package cz.cas.lib.vzb.reference.marc;

import core.index.IndexedNamedStore;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Indexed repository for indexed {@link IndexedRecord} and class {@link Record}
 */
@Repository
public class RecordStore extends IndexedNamedStore<Record, QRecord, IndexedRecord> {
    public RecordStore() {
        super(Record.class, QRecord.class, IndexedRecord.class);
    }

    @Getter
    public final String indexType = "record";

    /**
     * For constraint check; Must be unique name for owner
     **/
    public Record findEqualNameDifferentId(Record newRecord) {
        if (requireNonNull(newRecord).getName() == null) return null;

        Record entity = query()
                .select(qObject())
                .where(qObject().name.eq(newRecord.getName()))
                .where(qObject().id.ne(newRecord.getId()))
                .fetchFirst();
        detachAll();
        return entity;
    }


    @Override
    public IndexedRecord toIndexObject(Record record) {
        IndexedRecord indexedRecord = super.toIndexObject(record);
        indexedRecord.setUserId(record.getOwner().getId());
        indexedRecord.setLeader(record.getLeader());
        indexedRecord.setDataFields(record.getDataFields()
                .stream()
                .map(Datafield::getId)
                .collect(Collectors.toList()));
        return indexedRecord;
    }

}
