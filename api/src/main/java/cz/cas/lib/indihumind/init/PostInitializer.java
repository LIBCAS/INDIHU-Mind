package cz.cas.lib.indihumind.init;

import core.file.FileRef;
import core.file.FileRepository;
import core.report.ReportTemplate;
import core.report.ReportTemplateStore;
import core.report.ReportTemplateType;
import core.store.Transactional;
import core.util.Utils;
import cz.cas.lib.indihumind.util.StoreReindexer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
@Slf4j
public class PostInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${env}") private String env;
    @Inject private TestDataFiller testDataFiller;
    @Inject private StoreReindexer storeReindexer;
    @Inject private FileRepository fileRepository;
    @Inject private ReportTemplateStore reportTemplateStore;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            log.info(String.format("Starting application in mode: [%s]", env)); // staging / deploy / test

            if ("staging".equals(env)) {
                storeReindexer.removeIndexes();
                testDataFiller.clearDatabase();
                testDataFiller.createUsersAndData();
//                globalReindexer.reindex();
//                cardStore.dropReindex();
            }

            createReportTemplates();

//            ReportService.ReportDto reportDto = new ReportService.ReportDto();
//            reportDto.setIds(cardStore.findAll().stream().map(Card::getId).collect(Collectors.toList()));
//
//            reportDto.setType(ReportTemplateType.JSXML_TO_PDF);
//            reportService.createCardReport(reportDto);
//
//            reportDto.setType(ReportTemplateType.JSXML_TO_CSV);
//            reportService.createCardReport(reportDto);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createReportTemplates() throws IOException {
        log.info("Creating Report entities...");

        reportTemplate("templates/card.jrxml", "cardTemplate", "6764540c-826c-4c24-a850-e7827d28bd60", "cards-pdf-export", "card-report.pdf");
        reportTemplate("templates/cards-csv.jrxml", "cardsCsvTemplate", "eba436f6-93cb-44f8-aa08-b6452799be8f", "cards-csv-export", "cards-csv-report.pdf");

        log.info("Report templates created.");
    }

    private void reportTemplate(String jrxmlPath, String templateName, String reportId, String reportName, String reportFileName) throws IOException {
        // create FileRef for .jrxml by coppying it to specific directory
        String fileId = UUID.randomUUID().toString();
        log.debug("Creating citation report template {}.", fileId);
        InputStream citationStream = Utils.resource(jrxmlPath);
        FileRef fileRef = fileRepository.create(citationStream, templateName, ReportTemplateType.JSXML_TO_PDF.getContentType(), false, fileId);

        // create ReportTemplate that is later used by ReportGenerator
        ReportTemplate citationReportTemplate = new ReportTemplate();
        citationReportTemplate.setId(reportId);
        citationReportTemplate.setTemplate(fileRef);
        citationReportTemplate.setName(reportName);
        citationReportTemplate.setFileName(reportFileName);
        reportTemplateStore.save(citationReportTemplate);
    }

}
