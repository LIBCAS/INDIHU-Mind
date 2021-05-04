package cz.cas.lib.indihumind.citation.view;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.citation.CitationStore;
import cz.cas.lib.indihumind.citation.IndexedCitation;
import org.springframework.stereotype.Repository;

/**
 * @see CitationStore
 */
@Repository
public class CitationRefStore extends IndexedDatedStore<CitationRef, QCitationRef, IndexedCitation> {

    public CitationRefStore() {
        super(CitationRef.class, QCitationRef.class, IndexedCitation.class);
    }

    @Override
    public String getIndexType() {
        return CitationStore.INDEX_TYPE;
    }

}
