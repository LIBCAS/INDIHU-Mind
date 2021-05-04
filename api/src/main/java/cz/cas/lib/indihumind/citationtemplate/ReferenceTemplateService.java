package cz.cas.lib.indihumind.citationtemplate;

import core.domain.DomainObject;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.CitationStore;
import cz.cas.lib.indihumind.citation.IndexedCitation;
import cz.cas.lib.indihumind.citation.view.CitationRef;
import cz.cas.lib.indihumind.citationtemplate.fields.TemplateField;
import cz.cas.lib.indihumind.exception.NameAlreadyExistsException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.service.PdfExporter;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
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
import java.util.Set;
import java.util.stream.Collectors;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;
import static cz.cas.lib.indihumind.exception.NameAlreadyExistsException.ErrorCode.NAME_ALREADY_EXISTS;

@Service
@Slf4j
public class ReferenceTemplateService {

//    @Value("${vzb.marc.template.missing-data}")
//    private String MISSING_DATA_ERROR_TEXT;
    private String PDF_FILE_NAME;

    private Clock clock = Clock.systemDefaultZone(); // can be changed in tests with setter

    private ReferenceTemplateStore store;
    private CitationStore citationStore;
    private CardStore cardStore;
    private UserDelegate userDelegate;
    private PdfExporter pdfExporter;


    public ReferenceTemplate find(String id) {
        ReferenceTemplate entity = store.find(id);
        notNull(entity, () -> new MissingObject(ENTITY_IS_NULL, ReferenceTemplate.class, id));
        eq(entity.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, ReferenceTemplate.class, id));

        return entity;
    }

    @Transactional
    public ReferenceTemplate save(@NonNull ReferenceTemplate entity) {
        ReferenceTemplate fromDb = store.find(entity.getId());
        if (fromDb != null)
            eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, ReferenceTemplate.class, entity.getId()));

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        entity.setOwner(userDelegate.getUser());
        ReferenceTemplate nameExists = store.findEqualNameDifferentId(entity);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), ReferenceTemplate.class, nameExists.getId(), nameExists.getOwner()));

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
        return store.findByUser(userDelegate.getId());
    }

    /**
     * Generate PDF from {@link GeneratePdfDto} and send it to the user.
     *
     * @param dto with template and record IDs that are to be converted to PDF
     * @return response with stream and media type "application/pdf"
     */
    public ResponseEntity<InputStreamResource> generateWithCitations(GeneratePdfDto dto) {
        log.info("PDF citation generation for template '{}' with '{}' citations begins...", dto.getTemplateId(), dto.getIds().size());

        ReferenceTemplate template = find(dto.getTemplateId());

        List<Citation> citations = citationStore.findAllInList(dto.getIds());
        citations.forEach(record -> eq(record.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Citation.class, record.getId())));

        return generatePdf(template, citations);
    }

    public ResponseEntity<InputStreamResource> generateWithCards(GeneratePdfDto dto) {
        log.info("PDF citation generation for template '{}' with '{}' cards begins...", dto.getTemplateId(), dto.getIds().size());

        ReferenceTemplate template = find(dto.getTemplateId());

        List<Card> cards = cardStore.findAllInList(dto.getIds());
        cards.forEach(card -> eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, card.getId())));

        // set to remove duplicities, one citation can be referenced by multiple cards -> multiple identical citations in generated file
        Set<CitationRef> citationRefs = cards.stream().flatMap(card -> card.getRecords().stream()).collect(Collectors.toSet());
        List<Citation> citations = citationStore.findAllInList(citationRefs.stream().map(DomainObject::getId).collect(Collectors.toList()));
        citations.forEach(record -> eq(record.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Citation.class, record.getId())));

        return generatePdf(template, citations);
    }

    private ResponseEntity<InputStreamResource> generatePdf(ReferenceTemplate template, List<Citation> citations) {
        log.debug(String.format("Generating PDF file for template='%s' of user='%s' with citations='[%s]'",
                template.getId(), template.getOwner(), IndihuMindUtils.prettyPrintCollectionIds(citations)));

        // For each record, fill data into template and generate string with HTML tags by applying customizations
        List<String> htmlLines = new ArrayList<>();
        for (Citation citation : citations) {
            ReferenceTemplate templateWithData = createTemplateWithFilledData(template, citation);
            String formattedCitationAsHtml = createHtmlStringFromTemplateData(templateWithData);
            htmlLines.add(formattedCitationAsHtml);
        }

        InputStream generatedPdfFile = new BufferedInputStream(new ByteArrayInputStream(pdfExporter.export(htmlLines)));

        return IndihuMindUtils.createResponseEntityFromFile(generatedPdfFile, PDF_FILE_NAME + ".pdf", MediaType.APPLICATION_PDF);
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
                .map(field -> Typeface.formatData(IndihuMindUtils.escapeText(field.obtainTextualData()), field.getCustomizations()))
                .collect(Collectors.joining());
    }

    /**
     * Creates new (blank) template with filled data which are initialized in fields.
     *
     * If there is no suitable data for {@link TemplateField} then value from application.yml is used
     */
    private ReferenceTemplate createTemplateWithFilledData(ReferenceTemplate templateWithoutData, Citation record) {
        ReferenceTemplate template = ReferenceTemplate.blankCopyOf(templateWithoutData);

        template.marcFields().forEach(marcField -> marcField.initializeDataForTagAndCode(record));
        template.dateFields().forEach(dateField -> dateField.initializeCitationDate(clock));

        template.authorField().ifPresent(authorField -> authorField.initializeAuthorsNames(record));

        return template;
    }

//    private ReferenceTemplate createTemplateWithFilledDataAndShowMissingDataErrorText(ReferenceTemplate templateWithoutData, Citation record) {
//        ReferenceTemplate template = ReferenceTemplate.blankCopyOf(templateWithoutData);
//
//        template.marcFields().forEach(marcField -> marcField.initializeDataForTagAndCode(record, MISSING_DATA_ERROR_TEXT));
//        template.dateFields().forEach(dateField -> dateField.initializeCitationDate(clock));
//
//        template.authorField().ifPresent(authorField -> authorField.initializeAuthorsNames(record, MISSING_DATA_ERROR_TEXT));
//
//        return template;
//    }


    @Inject
    public void setCitationStore(CitationStore marcRecordStore) {
        this.citationStore = marcRecordStore;
    }

    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
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
