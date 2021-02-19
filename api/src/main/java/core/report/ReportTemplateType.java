package core.report;

import core.file.mime.SpecificMimeTypes;
import lombok.Getter;

/**
 * Supported report types
 *
 * Each report has a name, a template content type and a result content type.
 *
 * Default report type is obtainable with {@link #getDefault()}
 */
@Getter
public enum ReportTemplateType {
    JSXML_TO_PDF("PDF-jasper", ".pdf", SpecificMimeTypes.PDF, SpecificMimeTypes.PDF),
    JSXML_TO_DOCX("Word-jasper", ".docx", SpecificMimeTypes.DOCX, SpecificMimeTypes.DOCX),
    JSXML_TO_XLSX("Excel-jasper", ".xlsx", SpecificMimeTypes.XLSX, SpecificMimeTypes.XLSX),
    JSXML_TO_CSV("Csv-jasper", ".csv", SpecificMimeTypes.CSV, SpecificMimeTypes.CSV),
    JSXML_TO_HTML("Html-jasper", ".pdf", SpecificMimeTypes.HTML, SpecificMimeTypes.PDF);

    private final String label;
    private final String extension;
    private final String contentType;
    private final String resultType;

    ReportTemplateType(String label, String extension, String contentType, String resultType) {
        this.label = label;
        this.extension = extension;
        this.contentType = contentType;
        this.resultType = resultType;
    }

    public static ReportTemplateType getDefault() {
        return JSXML_TO_PDF;
    }

    public static ReportTemplateType typeOrGetDefault(ReportTemplateType type) {
        return type == null ? ReportTemplateType.getDefault() : type;
    }
}
