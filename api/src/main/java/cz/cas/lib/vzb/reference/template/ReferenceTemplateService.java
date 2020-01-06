package cz.cas.lib.vzb.reference.template;

import com.qkyrie.markdown2pdf.Markdown2PdfConverter;
import com.qkyrie.markdown2pdf.internal.exceptions.ConversionException;
import com.qkyrie.markdown2pdf.internal.exceptions.Markdown2PdfLogicException;
import com.qkyrie.markdown2pdf.internal.reading.SimpleStringMarkdown2PdfReader;
import com.qkyrie.markdown2pdf.internal.writing.SimpleFileMarkdown2PdfWriter;
import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.dto.GeneratePdfDto;
import cz.cas.lib.vzb.exception.MissingDataInRecordException;
import cz.cas.lib.vzb.exception.NameAlreadyExistsException;
import cz.cas.lib.vzb.reference.marc.Datafield;
import cz.cas.lib.vzb.reference.marc.Record;
import cz.cas.lib.vzb.reference.marc.RecordStore;
import cz.cas.lib.vzb.reference.marc.Subfield;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static core.util.Utils.*;
import static cz.cas.lib.vzb.reference.template.Customization.*;

@Service
@Slf4j
public class ReferenceTemplateService {

    private String pdfFileName;

    private ReferenceTemplateStore store;
    private RecordStore recordStore;
    private UserDelegate userDelegate;


    /**
     * Method generates formatted PDF and returns it as Stream
     * It retrieves and validates entities from DTO
     * <p>
     * First .md file (temporary) is created and filled with formatted data ({@link Customization} styles are simple enough for Markdown)
     * This temporary .md file is converted to temporary .pdf which is returned as a Stream for further handling.
     *
     * @return InputStream with generated PDF
     * @throws IOException when conversion from .md to .pdf has failed
     */
    public InputStream generatePdf(GeneratePdfDto dto) throws IOException {
        ReferenceTemplate template = store.find(dto.getTemplateId());
        notNull(template, () -> new MissingObject(ReferenceTemplate.class, dto.getTemplateId()));
        eq(template.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(ReferenceTemplate.class, template.getId()));

        List<Record> records = new ArrayList<>();
        dto.getIds().forEach(recordId -> {
            Record r = recordStore.find(recordId);
            notNull(r, () -> new MissingObject(Record.class, recordId));
            eq(r.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Record.class, recordId));
            records.add(r);
        });

        log.debug(String.format("Generating .pdf file for template='%s' of user='%s' with records='[%s]'",
                template.getId(), template.getOwner(), records.stream().map(Record::getId).collect(Collectors.joining(", "))));

        Path tempMarkdownFile = Files.createTempFile(null, ".md");
        Path tempPdfFile = Files.createTempFile(pdfFileName, ".pdf");

        // For each record, fill data into template and generate .md string by applying customizations
        List<String> markdownLines = new ArrayList<>();
        for (Record record : records) {
            ReferenceTemplate blankTemplate = ReferenceTemplate.blankCopyOf(template);
            fillRecordDataIntoTemplate(blankTemplate, record);
            markdownLines.add(transformTemplateWithDataToMarkdownString(blankTemplate));
        }
        Files.write(tempMarkdownFile, markdownLines);

        Markdown2PdfConverter markdown2PdfConverter = Markdown2PdfConverter.newConverter();
        try {
            markdown2PdfConverter
                    .readFrom(new SimpleStringMarkdown2PdfReader(String.join(System.lineSeparator(), markdownLines)))
                    .writeTo(new SimpleFileMarkdown2PdfWriter(tempPdfFile.toFile()))
                    .doIt();
        } catch (ConversionException | Markdown2PdfLogicException e) {
            throw new IOException("Markdown conversion to PDF has failed");
        }

        return new BufferedInputStream(new FileInputStream(tempPdfFile.toFile()));
    }


    public void delete(ReferenceTemplate entity) {
        store.delete(entity);
        log.debug("Deleting ReferenceTemplate " + entity.getName() + " of user " + entity.getOwner());
    }


    public ReferenceTemplate find(String id) {
        return store.find(id);
    }


    public Collection<ReferenceTemplate> findByUser(String id) {
        return store.findByUser(id);
    }


    public ReferenceTemplate save(ReferenceTemplate newReferenceTemplate) {
        ReferenceTemplate fromDb = store.find(newReferenceTemplate.getId());
        if (fromDb != null)
            eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(ReferenceTemplate.class, newReferenceTemplate.getId()));

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        ReferenceTemplate nameExists = store.findEqualNameDifferentId(newReferenceTemplate);
        isNull(nameExists, () -> new NameAlreadyExistsException(ReferenceTemplate.class, nameExists.getId(), nameExists.getName()));

        newReferenceTemplate.setOwner(userDelegate.getUser());
        store.save(newReferenceTemplate);
        return newReferenceTemplate;
    }


    public ReferenceTemplate preview(String templateId, String recordId) throws MissingDataInRecordException {
        ReferenceTemplate refTemplate = store.find(templateId);
        notNull(refTemplate, () -> new MissingObject(ReferenceTemplate.class, templateId));
        eq(refTemplate.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(ReferenceTemplate.class, templateId));

        Record record = recordStore.find(recordId);
        notNull(record, () -> new MissingObject(Record.class, recordId));
        eq(record.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Record.class, recordId));

        fillRecordDataIntoTemplate(refTemplate, record);

        return refTemplate;
    }


    public Result<ReferenceTemplate> findAll(Params params) {
        return store.findAll(params);
    }


    private String transformTemplateWithDataToMarkdownString(ReferenceTemplate template) {
        String pattern = template.getPattern();
        for (CustomizedField field : template.getFields()) {
            pattern = pattern.replaceFirst("\\$\\{\\?}", createMarkdownText(field.getData(), field.getCustomizations()));
        }
        return pattern;
    }


    private String createMarkdownText(List<String> dataList, Set<Customization> customizations) {
        if (customizations.isEmpty()) return dataList.get(0);
        String data;
        if (customizations.contains(CONCAT_COMMA)) {
            data = String.join(", ", dataList);
        } else if (customizations.contains(CONCAT_SPACE)) {
            data = String.join(" ", dataList);
        } else {
            data = dataList.get(0);
        }
        if (customizations.contains(UPPERCASE)) data = data.toUpperCase();
        if (customizations.contains(ITALIC)) data = "_" + data + "_";
        if (customizations.contains(BOLD)) data = "**" + data + "**";

        return data;
    }


    /**
     * Method fills data from record to template.
     * <p>
     * If there is no suitable data for {@link CustomizedField} then {@link MissingDataInRecordException} is thrown
     * If CustomizedField does not obtain neither {@link Customization#CONCAT_COMMA} nor {@link Customization#CONCAT_SPACE}
     * then only first element from list of data is assigned
     * </p>
     */
    private void fillRecordDataIntoTemplate(ReferenceTemplate template, Record record) throws MissingDataInRecordException {
        for (CustomizedField field : template.getFields()) {
            List<String> dataList = getDataFromRecordByTagAndCode(record, field.getTag(), field.getCode());
            if (field.getCustomizations().contains(CONCAT_COMMA) || field.getCustomizations().contains(CONCAT_SPACE)) {
                field.setData(dataList);
                continue;
            }
            field.setData(dataList.subList(0, 1));
        }

    }


    /**
     * Retrieves List of data for given combination of tag, code for record.
     * Returns List because both {@link Datafield} and {@link Subfield} can have multiple entries for tag and code.
     * (For code it is very rare situation)
     *
     * @throws MissingDataInRecordException when there is no data in record for given tag and code
     */
    private List<String> getDataFromRecordByTagAndCode(Record record, String tag, char code) throws MissingDataInRecordException {
        List<String> result = new ArrayList<>();
        List<Datafield> dataFields = record.getDataFieldsByTag(tag);
        for (Datafield df : dataFields) {
            for (Subfield subfield : df.getSubfieldsByCode(code)) {
                result.add(subfield.getData());
            }
        }

        if (result.isEmpty()) throw new MissingDataInRecordException(Record.class, record.getId(), tag, code);
        return result;
    }


    @Inject
    public void setRecordStore(RecordStore recordStore) {
        this.recordStore = recordStore;
    }

    @Inject
    public void setStore(ReferenceTemplateStore store) {
        this.store = store;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setPdfFileName(@Value("${vzb.marc.pdf-name}") String pdfFileName) {
        this.pdfFileName = pdfFileName;
    }
}
