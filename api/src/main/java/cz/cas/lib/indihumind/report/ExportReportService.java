package cz.cas.lib.indihumind.report;

import core.file.FileRef;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.report.ReportGenerator;
import core.report.ReportTemplateType;
import core.store.Transactional;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.CitationStore;
import cz.cas.lib.indihumind.citation.IndexedCitation;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static core.util.Utils.addPrefilter;

public class ExportReportService {

    private UserDelegate userDelegate;
    private CitationStore citationStore;
    private ReportGenerator reportGenerator;

    private String citationReportId;

    @Transactional
    public FileRef exportCitations(Params params, @Nullable ReportTemplateType exportTo) {
        addPrefilter(params, new Filter(IndexedCitation.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        Result<Citation> citations = citationStore.findAll(params);

        Map<String, Object> exportParams = new HashMap<>();
        exportParams.put(ReportGenerator.REPORT_ENTITIES, citations.getItems());

        return reportGenerator.generateToFileWithExtension(citationReportId, exportParams, false, ReportTemplateType.typeOrGetDefault(exportTo));
    }

    @Inject
    public void setCitationStore(CitationStore citationStore) {
        this.citationStore = citationStore;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    public void setCitationReportId(@Value("${vzb.report.citation}") String citationReportId) {
        this.citationReportId = citationReportId;
    }
}
