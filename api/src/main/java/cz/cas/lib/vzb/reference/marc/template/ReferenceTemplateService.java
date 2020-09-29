package cz.cas.lib.vzb.reference.marc.template;

import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.vzb.dto.GeneratePdfDto;
import cz.cas.lib.vzb.exception.NameAlreadyExistsException;
import cz.cas.lib.vzb.reference.marc.record.IndexedCitation;
import cz.cas.lib.vzb.reference.marc.record.MarcRecord;
import cz.cas.lib.vzb.reference.marc.record.MarcRecordStore;
import cz.cas.lib.vzb.reference.marc.template.field.TemplateField;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.service.PdfExporter;
import cz.cas.lib.vzb.util.IndihuMindUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static core.util.Utils.*;

@Service
@Slf4j
public class ReferenceTemplateService {

    @Value("${vzb.marc.template.missing-data}")
    private String MISSING_DATA_ERROR_TEXT;
    private String PDF_FILE_NAME;

    private Clock clock = Clock.systemDefaultZone(); // can be changed in tests with setter

    private ReferenceTemplateStore store;
    private MarcRecordStore marcRecordStore;
    private UserDelegate userDelegate;
    private PdfExporter pdfExporter;


    public ReferenceTemplate find(String id) {
        ReferenceTemplate entity = store.find(id);
        notNull(entity, () -> new MissingObject(ReferenceTemplate.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(ReferenceTemplate.class, id));

        return entity;
    }

    @Transactional
    public ReferenceTemplate save(@NonNull ReferenceTemplate entity) {
        ReferenceTemplate fromDb = store.find(entity.getId());
        if (fromDb != null)
            eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(ReferenceTemplate.class, entity.getId()));

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        entity.setOwner(userDelegate.getUser());
        ReferenceTemplate nameExists = store.findEqualNameDifferentId(entity);
        isNull(nameExists, () -> new NameAlreadyExistsException(ReferenceTemplate.class, nameExists.getId(), nameExists.getName(), nameExists.getOwner()));

        return store.save(entity);
    }

    @Transactional
    public void delete(String id) {
        ReferenceTemplate entity = find(id);

        log.debug("Deleting ReferenceTemplate " + entity.getName() + " of user " + entity.getOwner());
        store.delete(entity);
    }

    public Result<ReferenceTemplate> list(Params params) {
        addPrefilter(params, new Filter(IndexedCitation.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        return store.findAll(params);
    }

    public Collection<ReferenceTemplate> findByUser() {
        return store.findByUser(userDelegate.getUser().getId());
    }

    /**
     * Generate PDF from {@link GeneratePdfDto} and send it to the user.
     *
     * @param dto with template and record IDs that are to be converted to PDF
     * @return response with stream and media type "application/pdf"
     */
    public ResponseEntity<InputStreamResource> generatePdf(GeneratePdfDto dto) {
        ReferenceTemplate template = find(dto.getTemplateId());

        // BriefRecords are skipped automatically by store
        List<MarcRecord> records = marcRecordStore.findAllInList(dto.getIds());
        records.forEach(record -> eq(record.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(MarcRecord.class, record.getId())));

        log.debug(String.format("Generating PDF file for template='%s' of user='%s' with records='[%s]'",
                template.getId(), template.getOwner(), IndihuMindUtils.prettyPrintCollectionIds(records)));

        // For each record, fill data into template and generate string with HTML tags by applying customizations
        List<String> htmlLines = new ArrayList<>();
        for (MarcRecord record : records) {
            ReferenceTemplate templateWithData = createTemplateWithFilledData(template, record);
            String formattedCitationAsHtml = createHtmlStringFromTemplateData(templateWithData);
            htmlLines.add(formattedCitationAsHtml);
        }

        InputStream generatedPdfFile = new BufferedInputStream(new ByteArrayInputStream(pdfExporter.export(htmlLines)));

        return IndihuMindUtils.createResponseEntityPdfFile(generatedPdfFile, PDF_FILE_NAME);
    }


    /**
     * Create single HTML string from fields' data and customizations
     *
     * E.g. template has fields:
     * {MARC 020a, BOLD} {COMMA} {SPACE} {GENERATED_DATE, ITALIC}
     * {COMMA} {SPACE} {ONLINE} {SPACE} {MARC 300a UPPERCASE}
     *
     * Method returns HTML: "<b>ISBN-NUMBER</b>, [<i>2020-07-10</i>], [online] BRNO
     *
     * @param templateWithData template that has data filled into fields and are retrievable by getData()
     * @return string with HTML tagged data according to customizations
     */
    private String createHtmlStringFromTemplateData(ReferenceTemplate templateWithData) {
        return templateWithData.getFields().stream()
                .map(field -> Typeface.formatData(IndihuMindUtils.escapeText(field.getData()), field.getCustomizations()))
                .collect(Collectors.joining());
    }

    /**
     * Creates new (blank) template with filled data which are initialized in fields.
     *
     * If there is no suitable data for {@link TemplateField} then value from application.yml is used
     */
    private ReferenceTemplate createTemplateWithFilledData(ReferenceTemplate templateWithoutData, MarcRecord record) {
        ReferenceTemplate template = ReferenceTemplate.blankCopyOf(templateWithoutData);

        template.marcFields().forEach(marcField -> marcField.initializeDataForTagAndCode(record, MISSING_DATA_ERROR_TEXT));
        template.dateFields().forEach(dateField -> dateField.initializeCitationDate(clock));

        template.authorField().ifPresent(authorField -> authorField.initializeAuthorsNames(record, MISSING_DATA_ERROR_TEXT));

        return template;
    }


    @Inject
    public void setRecordStore(MarcRecordStore marcRecordStore) {
        this.marcRecordStore = marcRecordStore;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setPdfFileName(@Value("${vzb.marc.pdf-name}") String pdfFileName) {
        this.PDF_FILE_NAME = pdfFileName;
    }

    @Inject
    public void setPdfExporter(PdfExporter pdfExporter) {
        this.pdfExporter = pdfExporter;
    }

    @Inject
    public void setStore(ReferenceTemplateStore store) {
        this.store = store;
    }

    // For testing purposes
    public void setClock(Clock clock) {
        this.clock = clock;
    }

}
