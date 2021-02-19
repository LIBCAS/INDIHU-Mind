package core.report;

import core.index.IndexedDatedStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ReportTemplateStore extends IndexedDatedStore<ReportTemplate, QReportTemplate, IndexedReport> {

    public ReportTemplateStore() {
        super(ReportTemplate.class, QReportTemplate.class, IndexedReport.class);
    }

    public final String indexType = "report";

    @Override
    public String getIndexType() {
        return indexType;
    }

    @Override
    public IndexedReport toIndexObject(ReportTemplate entity) {
        IndexedReport indexed = super.toIndexObject(entity);
        if (entity.getName() != null) indexed.setName(entity.getName());
        if (entity.getFileName() != null) indexed.setFileName(entity.getFileName());
        return indexed;
    }

}
