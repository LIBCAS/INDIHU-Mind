package cz.cas.lib.indihumind.init;

import core.file.FileRef;
import core.file.FileRepository;
import core.index.global.GlobalReindexer;
import core.report.ReportTemplate;
import core.report.ReportTemplateStore;
import core.report.ReportTemplateType;
import core.store.Transactional;
import core.util.Utils;
import cz.cas.lib.indihumind.card.CardStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class PostInitializer implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${env}") private String env;
    @Inject private TestDataFiller testDataFiller;
    @Inject private GlobalReindexer globalReindexer;
    @Inject private CardStore cardStore;
    @Inject private FileRepository fileRepository;
    @Inject private ReportTemplateStore reportTemplateStore;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            log.info(String.format("Starting application in mode: [%s]", env)); // staging / deploy / test

            if ("staging".equals(env)) {
                cardStore.removeAllIndexes();
                globalReindexer.removeIndexes(null);
                testDataFiller.clearDatabase();
                testDataFiller.createUsersAndData();
//                globalReindexer.reindex();
//                cardStore.dropReindex();
            }

            createReportEntities();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createReportEntities() throws IOException {
        log.info("Creating Report entities...");

        log.debug("Creating CITATION report template.");
        InputStream citationStream = Utils.resource("templates/citations.jrxml");
        FileRef citationTemplate = fileRepository.create(citationStream, "citationTemplate", ReportTemplateType.JSXML_TO_PDF.getContentType(), false);

        ReportTemplate citationReportTemplate = new ReportTemplate();
        citationReportTemplate.setId("f37d8a32-6ee0-4303-b367-58b8c4daf41b");
        citationReportTemplate.setTemplate(citationTemplate);
        citationReportTemplate.setName("Citation Report");
        citationReportTemplate.setFileName("citations_report.pdf");
        reportTemplateStore.save(citationReportTemplate);

        log.info("Report templates created.");
    }
}
