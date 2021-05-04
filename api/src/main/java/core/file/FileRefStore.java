package core.file;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.util.Reindexable;
import lombok.Getter;
import org.springframework.stereotype.Repository;

/**
 * Implementation of {@link IndexedDatedStore} for storing {@link FileRef} and indexing {@link IndexedFileRef}.
 */
@Repository
public class FileRefStore extends IndexedDatedStore<FileRef, QFileRef, IndexedFileRef> implements Reindexable {

    public FileRefStore() {
        super(FileRef.class, QFileRef.class, IndexedFileRef.class);
    }

    @Getter
    private final String indexType = "fileRef";


    @Override
    public IndexedFileRef toIndexObject(FileRef o) {
        IndexedFileRef indexedFileRef = super.toIndexObject(o);

        indexedFileRef.setName(o.getName());
        indexedFileRef.setContent(o.getContent());

        return indexedFileRef;
    }

    @Override
    public void reindexEverything() {
        dropReindex();
    }

    @Override
    public void removeAllDataFromIndex() {
        removeAllIndexes();
    }
}
