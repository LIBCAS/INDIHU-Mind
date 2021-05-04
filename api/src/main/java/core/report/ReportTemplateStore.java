package core.report;

import core.index.IndexedDatedStore;
import cz.cas.lib.indihumind.util.Reindexable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ReportTemplateStore extends IndexedDatedStore<ReportTemplate, QReportTemplate, IndexedReport> implements Reindexable {

    public ReportTemplateStore() {
        super(ReportTemplate.class, QReportTemplate.class, IndexedReport.class);
    }

    public static final String INDEX_TYPE = "report";

    @Override
    public String getIndexType() {
        return INDEX_TYPE;
    }

    @Override
    public IndexedReport toIndexObject(ReportTemplate entity) {
        IndexedReport indexed = super.toIndexObject(entity);
        if (entity.getName() != null) indexed.setName(entity.getName());
        if (entity.getFileName() != null) indexed.setFileName(entity.getFileName());
        return indexed;
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
