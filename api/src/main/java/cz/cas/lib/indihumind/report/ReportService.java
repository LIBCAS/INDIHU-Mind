package cz.cas.lib.indihumind.report;

import core.exception.ForbiddenObject;
import core.file.FileRef;
import core.file.FileRepository;
import core.report.ReportGenerator;
import core.report.ReportTemplateType;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
import cz.cas.lib.indihumind.validation.Uuid;
import lombok.Getter;
import lombok.Setter;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.*;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.util.Utils.eq;

@Service
public class ReportService {

    private String CARD_PDF_REPORT_ID;
    private String CARD_CSV_REPORT_ID;

    private UserDelegate userDelegate;
    private CardStore cardStore;
    private ReportGenerator reportGenerator;
    private FileRepository fileRepository;

    @Transactional
    public ResponseEntity<InputStreamResource> createCardReport(ReportDto dto) {
        List<Card> cards = cardStore.findAllInList(dto.getIds());
        cards.forEach(card -> eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, card.getId())));

        FileRef fileRef;
        switch (dto.getType()) {
            case JSXML_TO_PDF:
                fileRef = pdfCardReport(cards);
                break;
            case JSXML_TO_CSV:
                fileRef = csvCardReport(cards);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + dto.getType());
        }

        FileRef reportWithStream = fileRepository.get(fileRef.getId());
        return IndihuMindUtils.createResponseEntityFromFile(reportWithStream.getStream(), reportWithStream.getName(), MediaType.valueOf(reportWithStream.getContentType()));
    }


    private FileRef pdfCardReport(List<Card> cards) {
        return reportGenerator.generateToFileWithExtension(
                CARD_PDF_REPORT_ID,
                cards, // will be transformed into [ {"entity" : object}, {"entity" : object2} ]
                new HashMap<>(),
                ReportTemplateType.JSXML_TO_PDF
        );
    }

    private FileRef csvCardReport(List<Card> cards) {
        // to create jasper table provide data source as a param
        Map<String, Object> exportParams = new HashMap<>();
        exportParams.put(ReportGenerator.DATASOURCE_PARAMETER, new JRBeanCollectionDataSource(cards));

        return reportGenerator.generateToFileWithExtension(
                CARD_CSV_REPORT_ID,
                Collections.emptyList(),
                exportParams,
                ReportTemplateType.JSXML_TO_CSV
        );
    }


    @Getter
    @Setter
    public static class ReportDto {
        @NotEmpty
        private List<@Uuid String> ids = new ArrayList<>();
        @NotNull
        private ReportTemplateType type;
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
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Inject
    public void setReportGenerator(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    @Inject
    public void setCardsCsvReportId(@Value("${vzb.report.cards-csv}") String cardsCsvReportId) {
        this.CARD_CSV_REPORT_ID = cardsCsvReportId;
    }

    @Inject
    public void setCardReportId(@Value("${vzb.report.card}") String cardReportId) {
        this.CARD_PDF_REPORT_ID = cardReportId;
    }


}
