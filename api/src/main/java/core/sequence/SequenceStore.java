package core.sequence;

import core.Changed;
import core.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
@Changed("not an indexed store")
public class SequenceStore extends DatedStore<Sequence, QSequence> {

    public SequenceStore() {
        super(Sequence.class, QSequence.class);
    }
}
