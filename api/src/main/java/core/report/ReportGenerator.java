package core.report;

import core.exception.GeneralException;
import core.exception.MissingAttribute;
import core.exception.MissingObject;
import core.file.FileRef;
import core.file.FileRepository;
import core.report.exception.ReportGenerateException;
import core.report.exception.UnsupportedTemplateException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static core.exception.MissingAttribute.ErrorCode.MISSING_ATTRIBUTE;
import static core.util.Utils.notNull;

/**
 * Report generator
 */
@Service
public class ReportGenerator {

    /** Key under which data for jasper are stored. Data must be stored in {@link Collection}. */
    public static final String REPORT_ENTITIES = "entities";
    /** Key under which CSV delimiter is stored. Must be of {@link String} type. */
    public static final String CSV_DELIMITER = "csvDelimiter";

    private ReportTemplateStore store;
    private FileRepository repository;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());

    /**
     * Generates {@link ReportTemplate} specified by id and {@link Map} of params and returns it to the caller.
     *
     * @param reportId Id of the {@link ReportTemplate} to generate
     * @param params   User supplied parameters
     * @return {@link GeneratedReportDto} containing generated content
     * @throws UnsupportedTemplateException If template file has unsupported content type
     */
    public GeneratedReportDto generate(String reportId, Map<String, Object> params, ReportTemplateType exportTo) {
        ReportTemplate reportTemplate = store.find(reportId);
        notNull(reportTemplate, () -> new MissingObject(MissingObject.ErrorCode.ENTITY_IS_NULL, ReportTemplate.class, reportId));

        Map<String, Object> allParams = gatherParameters(params);

        FileRef template = reportTemplate.getTemplate();
        notNull(template, () -> new MissingAttribute(MISSING_ATTRIBUTE, ReportTemplate.class, "template"));

        byte[] content;
        try {
            repository.reset(template);

            JasperPrint compiledJasperWithData = generateJasper(template, allParams);

            switch (exportTo) {
                case JSXML_TO_DOCX:
                    content = JasperExportAggregator.generateJasperWord(compiledJasperWithData);
                    break;
                case JSXML_TO_XLSX:
                    content = JasperExportAggregator.generateJasperExcel(compiledJasperWithData);
                    break;
                case JSXML_TO_PDF:
                    content = JasperExportAggregator.generateJasperPdf(compiledJasperWithData);
                    break;
                case JSXML_TO_HTML:
                    content = JasperExportAggregator.generateJasperHtml(compiledJasperWithData);
                    break;
                case JSXML_TO_CSV:
                    content = JasperExportAggregator.generateJasperCsv(compiledJasperWithData, retrieveDelimiter(allParams));
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown export type");
            }
        } catch (JRException e) {
            throw new GeneralException(e);
        } finally {
            repository.close(template);
        }

        return new GeneratedReportDto(content, exportTo.getResultType(), reportTemplate.getFileName());
    }

    /**
     * Generates {@link ReportTemplate} specified by id and {@link Map} of params and stores it as a file in
     * {@link FileRepository}.
     *
     * @param reportId Id of the {@link ReportTemplate} to generate
     * @param index    Should the content be indexed
     * @param params   User supplied parameters
     * @return {@link FileRef} containing reference to generated content
     * @throws UnsupportedTemplateException If template file has unsupported content type
     */
    public FileRef generateToFile(String reportId, Map<String, Object> params, boolean index, ReportTemplateType exportTo) {

        GeneratedReportDto generatedReportDto = generate(reportId, params, exportTo);

        ByteArrayInputStream contentStream = new ByteArrayInputStream(generatedReportDto.getContent());

        return repository.create(contentStream, generatedReportDto.getName(), generatedReportDto.getContentType(), index);
    }

    public FileRef generateToFileWithExtension(String reportId, Map<String, Object> params, boolean index, ReportTemplateType exportTo) {
        GeneratedReportDto generatedReportDto = generate(reportId, params, exportTo);

        ByteArrayInputStream contentStream = new ByteArrayInputStream(generatedReportDto.getContent());

        String fileNameBase = FilenameUtils.removeExtension(generatedReportDto.getName());
        String extension = exportTo.getExtension();
        String fileWithNewExtension = fileNameBase + (extension.startsWith(".") ? extension : "." + extension);

        return repository.create(contentStream, fileWithNewExtension, exportTo.getContentType(), index);
    }


    private JasperPrint generateJasper(FileRef template, Map<String, Object> params) {
        try {
            JasperReport compiledTemplate = JasperCompileManager.compileReport(template.getStream());

            Object entities = params.get(REPORT_ENTITIES);
            if (entities == null)
                throw new ReportGenerateException("Data for report template were not provided under key:" + REPORT_ENTITIES);

            return JasperFillManager.fillReport(compiledTemplate, params, new JRBeanCollectionDataSource((Collection<?>) entities));
        } catch (JRException e) {
            throw new ReportGenerateException("Unable to compile Jasper report", e);
        }
    }

    private Map<String, Object> gatherParameters(Map<String, Object> params) {
        Map<String, Object> allParams = new HashMap<>();

        if (params != null) {
            allParams.putAll(params);
        }

        // add helpers
        allParams.put("dateFormatter", dateFormatter);
        allParams.put("timeFormatter", timeFormatter);
        allParams.put("dateTimeFormatter", dateTimeFormatter);

        return allParams;
    }

    private String retrieveDelimiter(Map<String, Object> params) {
        String defaultDelimiter = ";";
        Object delimiter = params.get(CSV_DELIMITER);
        return delimiter == null ? defaultDelimiter : (String) delimiter;
    }

    @Inject
    public void setStore(ReportTemplateStore store) {
        this.store = store;
    }

    @Inject
    public void setRepository(FileRepository repository) {
        this.repository = repository;
    }

}
