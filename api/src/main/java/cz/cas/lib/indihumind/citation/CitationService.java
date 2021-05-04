package cz.cas.lib.indihumind.citation;

import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.citation.dto.CreateCitationDto;
import cz.cas.lib.indihumind.citation.dto.UpdateCitationDto;
import cz.cas.lib.indihumind.citation.view.CitationRef;
import cz.cas.lib.indihumind.citation.view.CitationRefStore;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.document.AttachmentFileStore;
import cz.cas.lib.indihumind.exception.NameAlreadyExistsException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;
import static cz.cas.lib.indihumind.exception.NameAlreadyExistsException.ErrorCode.NAME_ALREADY_EXISTS;

@Service
@Slf4j
public class CitationService {

    private MarcFieldsValidator marcFieldsValidator;
    private AttachmentFileStore documentStore;
    private UserDelegate userDelegate;
    private CardStore cardStore;

    private CitationStore store;
    private CitationRefStore citationRefStore;

    public Citation find(String id) {
        Citation marcRecord = store.find(id);
        notNull(marcRecord, () -> new MissingObject(ENTITY_IS_NULL, Citation.class, id));
        eq(marcRecord.getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Citation.class, id));

        return marcRecord;
    }

    @Transactional
    public Citation create(CreateCitationDto dto) {
        marcFieldsValidator.validate(dto.getDataFields());

        Citation citation = new Citation();
        citation.setOwner(userDelegate.getUser());
        transformDto(dto, citation);

        return store.save(citation);
    }

    @Transactional
    public Citation update(UpdateCitationDto dto) {
        marcFieldsValidator.validate(dto.getDataFields());

        Citation citation = find(dto.getId());
        transformDto(dto, citation);

        return store.save(citation);
    }


    @Transactional
    public void delete(String id) {
        Citation entity = find(id);

        entity.setLinkedCards(Collections.emptyList());
        entity.setDocuments(Collections.emptySet());
        store.save(entity);

        store.delete(entity);
    }


    public Result<CitationRef> findAll(Params params) {
        addPrefilter(params, new Filter(IndexedCitation.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        return citationRefStore.findAll(params);
    }

    public Collection<Citation> findByUser() {
        return store.findByUser(userDelegate.getId());
    }


    /**
     * Transform dto into citation with checking validity of other entities (cards and documents)
     */
    private void transformDto(CreateCitationDto dto, Citation citation) {
        citation.setName(dto.getName());
        citation.setDataFields(dto.getDataFields());
        citation.setContent(dto.getContent());

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        Citation nameExists = store.findEqualNameDifferentId(citation);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), Citation.class, nameExists.getId(), nameExists.getOwner()));

        List<AttachmentFile> documents = documentStore.findAllInList(dto.getDocuments());
        documents.forEach(document -> eq(document.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, AttachmentFile.class, document.getId())));
        citation.setDocuments(documents);

        List<Card> cards = cardStore.findAllInList(dto.getLinkedCards());
        cards.forEach(card -> eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, card.getId())));
        citation.setLinkedCards(cards);
    }


    @Inject
    public void setMarcFieldsProvider(MarcFieldsValidator marcFieldsValidator) {
        this.marcFieldsValidator = marcFieldsValidator;
    }

    @Inject
    public void setCardStore(CardStore cardStore) {
        this.cardStore = cardStore;
    }

    @Inject
    public void setStore(CitationStore store) {
        this.store = store;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setDocumentStore(AttachmentFileStore documentStore) {
        this.documentStore = documentStore;
    }

    @Inject
    public void setCitationRefStore(CitationRefStore citationRefStore) {
        this.citationRefStore = citationRefStore;
    }
}
