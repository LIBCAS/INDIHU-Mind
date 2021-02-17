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
import cz.cas.lib.indihumind.document.AttachmentFileStore;
import cz.cas.lib.indihumind.exception.NameAlreadyExistsException;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
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
    private CitationStore store;
    private AttachmentFileStore documentStore;
    private UserDelegate userDelegate;
    private CardStore cardStore;

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
        citation.setName(dto.getName());
        citation.setDataFields(dto.getDataFields());
        citation.setContent(dto.getContent());

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        Citation nameExists = store.findEqualNameDifferentId(citation);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), Citation.class, nameExists.getId(), nameExists.getOwner()));

        citation.setDocuments(asSet(documentStore.findAllInList(dto.getDocuments())));
        Citation savedRecord = store.save(citation);

        // Card is owner, save in cardStore
        List<Card> linkedCards = cardStore.findAllInList(dto.getLinkedCards());
        linkedCards.forEach(card -> card.addCitation(savedRecord));
        cardStore.save(linkedCards);

        return savedRecord;
    }


    @Transactional
    public Citation update(UpdateCitationDto dto) {
        marcFieldsValidator.validate(dto.getDataFields());

        Citation citation = find(dto.getId());
        citation.setName(dto.getName());
        citation.setDataFields(dto.getDataFields());
        citation.setContent(dto.getContent());

        // enforce addUniqueConstraint `vzb_record_of_user_uniqueness` of columnNames="name,owner_id"
        Citation nameExists = store.findEqualNameDifferentId(citation);
        isNull(nameExists, () -> new NameAlreadyExistsException(NAME_ALREADY_EXISTS, nameExists.getName(), Citation.class, nameExists.getId(), nameExists.getOwner()));

        citation.setDocuments(asSet(documentStore.findAllInList(dto.getDocuments())));

        if (IndihuMindUtils.isCollectionModified(citation.getLinkedCards(), dto.getLinkedCards())) {
            List<Card> oldCards = cardStore.findCardsOfCitation(citation);
            oldCards.forEach(card -> card.removeCitation(citation));
            cardStore.save(oldCards);

            List<Card> newCards = cardStore.findAllInList(dto.getLinkedCards());
            newCards.forEach(card -> card.addCitation(citation));
            cardStore.save(newCards);
        }

        return store.save(citation);
    }


    @Transactional
    public void delete(String id) {
        Citation entity = find(id);

        List<Card> cardsOfCitation = cardStore.findCardsOfCitation(entity);
        log.debug(String.format("Deleting record %s of user %s and removing it from cards=[%s]",
                entity.getName(), entity.getOwner(), IndihuMindUtils.prettyPrintCollectionIds(cardsOfCitation)));

        cardsOfCitation.forEach(card -> card.removeCitation(entity));
        cardStore.save(cardsOfCitation);

        entity.setDocuments(Collections.emptySet());
        store.save(entity);

        store.delete(entity);
    }


    public Result<Citation> findAll(Params params) {
        addPrefilter(params, new Filter(IndexedCitation.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        return store.findAll(params);
    }

    public Collection<Citation> findByUser() {
        return store.findByUser(userDelegate.getId());
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

}
