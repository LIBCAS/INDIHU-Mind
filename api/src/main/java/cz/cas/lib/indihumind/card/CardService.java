package cz.cas.lib.indihumind.card;

import core.config.SolrConfig;
import core.exception.ForbiddenObject;
import core.exception.ForbiddenOperation;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.sequence.Generator;
import core.store.Transactional;
import cz.cas.lib.indihumind.card.dto.CardSearchResultDto;
import cz.cas.lib.indihumind.card.dto.CreateCardDto;
import cz.cas.lib.indihumind.card.dto.UpdateCardContentDto;
import cz.cas.lib.indihumind.card.dto.UpdateCardDto;
import cz.cas.lib.indihumind.card.view.CardListDto;
import cz.cas.lib.indihumind.card.view.CardListDtoStore;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.card.view.CardRefStore;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeStore;
import cz.cas.lib.indihumind.cardattribute.AttributeTemplate;
import cz.cas.lib.indihumind.cardattribute.AttributeTemplateStore;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcontent.CardContent;
import cz.cas.lib.indihumind.cardcontent.CardContentStore;
import cz.cas.lib.indihumind.cardcontent.view.CardContentListDto;
import cz.cas.lib.indihumind.cardcontent.view.CardContentListDtoStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.cardtemplate.CardTemplate;
import cz.cas.lib.indihumind.cardtemplate.CardTemplateStore;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.document.AttachmentFileStore;
import cz.cas.lib.indihumind.document.QuotaVerifier;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.util.BulkFlagSetDto;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.ForbiddenOperation.ErrorCode.CARD_IN_TRASH_BIN;
import static core.exception.ForbiddenOperation.ErrorCode.CARD_NOT_IN_TRASH_BIN;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;

@Slf4j
@Service
public class CardService {

    private CardStore store;
    private CardIndexFacade cardIndex;
    private CardListDtoStore cardListDtoStore;
    private CardRefStore cardRefStore;
    private CardContentListDtoStore cardContentListDtoStore;
    private Generator generator;
    private AttributeStore attributeStore;
    private CardTemplateStore cardTemplateStore;
    private CardContentStore cardContentStore;
    private AttributeTemplateStore attributeTemplateStore;
    private UserDelegate userDelegate;
    private AttachmentFileStore documentStore;
    private QuotaVerifier quotaVerifier;


    /**
     * Find Card WITHOUT LAZY entities initialized.
     */
    public Card find(String id) {
        Card card = store.find(id);
        notNull(card, () -> new MissingObject(ENTITY_IS_NULL, Card.class, id));
        eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, id));

        return card;
    }

    public CardNote findCardNote(String cardId) {
        Card cardWithNote = store.findWithCardNote(cardId);
        notNull(cardWithNote, () -> new MissingObject(ENTITY_IS_NULL, Card.class, cardId));
        eq(cardWithNote.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, cardId));

        return cardWithNote.getStructuredNote();
    }

    public Result<CardListDto> findAll(Params params) {
        return cardListDtoStore.findAll(params);
    }

    @Transactional
    public CardContent createCard(CreateCardDto dto) {
        quotaVerifier.verify(IndihuMindUtils.stringByteSize(dto.getNote()));
        User loggedInUser = userDelegate.getUser();

        Card card = new Card();
        card.setOwner(loggedInUser);
        card.setId(dto.getId());
        card.setName(dto.getName());
        card.setStructuredNote(new CardNote(dto.getNote()));
        card.setRawNote(dto.getRawNote());
        card.setCategories(dto.getCategories().stream().map(Category::new).collect(Collectors.toSet()));
        card.setLabels(dto.getLabels().stream().map(Label::new).collect(Collectors.toSet()));
        card.setLinkedCards(dto.getLinkedCards().stream().map(Card::new).filter(linkedCard -> !linkedCard.getId().equals(dto.getId())).map(Card::toReference).collect(Collectors.toSet()));
        card.setPid(generator.generatePlain(loggedInUser.assembleCardPidSequenceId()));
        card.setRecords(dto.getRecords().stream().map(Citation::new).map(Citation::toReference).collect(Collectors.toSet()));
        card.setDocuments(documentStore.findAllInList(dto.getFiles()).stream().map(AttachmentFile::toReference).collect(Collectors.toSet()));
        store.save(card);

        CardContent v1 = new CardContent();
        v1.setLastVersion(true);
        v1.setCard(card);
        cardContentStore.save(v1);

        dto.getAttributes().forEach(a -> a.setCardContent(v1));
        attributeStore.save(dto.getAttributes());

        log.debug(String.format("Creating and indexing new Card %s for user %s", card.getId(), card.getOwner()));
        store.save(card);

        v1.setAttributes(new HashSet<>(dto.getAttributes()));
        return v1;
    }

    @Transactional
    public CardContent updateCard(String cardId, UpdateCardDto dto) {
        Card fromDb = store.find(cardId);
        notNull(fromDb, () -> new MissingObject(ENTITY_IS_NULL, Card.class, cardId));
        eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, cardId));
        eq(fromDb.inTrashBin(), Boolean.FALSE, () -> new ForbiddenOperation(CARD_IN_TRASH_BIN, Card.class, cardId));

        long newSize = IndihuMindUtils.stringByteSize(dto.getNote());
        long oldSize = store.cardNoteSizeForCard(fromDb.getId());
        quotaVerifier.verify(newSize - oldSize);

        fromDb.setName(dto.getName());
        fromDb.setStructuredNote(new CardNote(dto.getNote()));
        fromDb.setRawNote(dto.getRawNote());
        fromDb.setCategories(dto.getCategories().stream().map(Category::new).collect(Collectors.toSet()));
        fromDb.setLabels(dto.getLabels().stream().map(Label::new).collect(Collectors.toSet()));
        fromDb.setRecords(dto.getRecords().stream().map(Citation::new).map(Citation::toReference).collect(Collectors.toSet()));
        fromDb.setDocuments(documentStore.findAllInList(dto.getFiles()).stream().map(AttachmentFile::toReference).collect(Collectors.toSet()));

        fromDb.setLinkedCards(dto.getLinkedCards().stream()
                .map(Card::new)
                .filter(linkedCard -> !linkedCard.getId().equals(cardId))
                .map(Card::toReference)
                .collect(Collectors.toSet()));

        fromDb.setLinkingCards(dto.getLinkingCards().stream()
                .map(Card::new)
                .filter(linkingCard -> !linkingCard.getId().equals(cardId))
                .map(Card::toReference)
                .collect(Collectors.toSet()));

        store.save(fromDb);

        return cardContentStore.findLastVersionOfCard(cardId);
    }

    @Transactional
    public CardContent updateCardContent(String cardId, UpdateCardContentDto dto) {
        Card fromDb = store.find(cardId);
        notNull(fromDb, () -> new MissingObject(ENTITY_IS_NULL, Card.class, cardId));
        eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, cardId));

        CardContent lastContentVersion = cardContentStore.findLastVersionOfCard(cardId);
        if (dto.isNewVersion()) {
            lastContentVersion.setLastVersion(false);
            cardContentStore.save(lastContentVersion);

            CardContent newContent = new CardContent();
            newContent.setId(UUID.randomUUID().toString());
            newContent.setLastVersion(true);
            newContent.setOrigin(lastContentVersion);
            newContent.setCard(lastContentVersion.getCard());
            cardContentStore.save(newContent);

            dto.getAttributes().forEach(a -> {
                a.setCardContent(newContent);
                //assigns new ids to the attributes of the content
                a.setId(UUID.randomUUID().toString());
            });
            attributeStore.save(dto.getAttributes());
            newContent.setAttributes(new HashSet<>(dto.getAttributes()));
            store.save(fromDb);
            return newContent;
        } else {
            lastContentVersion.getAttributes().stream()
                    .filter(a -> !dto.getAttributes().contains(a))
                    .forEach(a -> attributeStore.hardDelete(a));
            dto.getAttributes().forEach(a -> a.setCardContent(lastContentVersion));
            lastContentVersion.setAttributes(new HashSet<>(dto.getAttributes()));
            attributeStore.save(lastContentVersion.getAttributes());
            cardContentStore.save(lastContentVersion);
            store.save(fromDb);
            return lastContentVersion;
        }
    }

    @Transactional
    public Result<Card> deleteAllCardsFromTrashBin() {
        List<Card> trashedCards = store.findCardsFromTrashBin(userDelegate.getId());
        for (Card card : trashedCards) {
            eq(card.inTrashBin(), Boolean.TRUE, () -> new ForbiddenOperation(CARD_NOT_IN_TRASH_BIN, Card.class, card.getId()));
        }
        long numberOfRemovedCards = trashedCards.size();
        for (Card card : trashedCards) {
            unlinkRelatedEntities(card);
            store.delete(card);
        }
        return Result.with(Collections.emptyList(), numberOfRemovedCards); // json object to wrap deleted cards count
    }

    @Transactional
    public void deleteSingleCardFromTrashBin(String cardId) {
        Card card = find(cardId);
        eq(card.inTrashBin(), Boolean.TRUE, () -> new ForbiddenOperation(CARD_NOT_IN_TRASH_BIN, Card.class, card.getId()));
        unlinkRelatedEntities(card);
        store.delete(card);
    }

    /**
     * Simple "Karta" query with highlighting.
     *
     * @implNote Querying is done with raw SolrJ instead of Spring-Solr-Data because of bug when querying with
     *         {@link SolrTemplate#queryForHighlightPage} which throws exception because it cannot find "id" field
     *         even though we are extending IndexDomainObject.
     *         https://github.com/spring-projects/spring-data-solr/issues/729
     *
     *         The method {@link SolrTemplate#query} is NOT bugged, however it does not return any highlighting
     *         results even when explicitly requested.
     *         Therefore it must be done with "raw" SolrJ - {@link SolrConfig#solrClient()}.
     */
    public Result<CardSearchResultDto> simpleHighlightSearch(String queryString, int page, int pageSize, String userId) {
        CardIndexFacade.IndexSearchResult indexSearchResult = cardIndex.highlightedSearch(queryString, userId, page, pageSize);
        List<CardSearchResultDto> dtos = indexSearchResult.transform();
        return Result.with(dtos, dtos.size());
    }

    @Transactional
    public void switchSoftDeletionFlag(BulkFlagSetDto dto) {
        List<Card> affectedCards = new ArrayList<>();

        for (String cardId : dto.getIds()) {
            Card card = find(cardId);
            card.setDeleted(dto.getValue() ? Instant.now() : null);
            affectedCards.add(card);
        }

        store.save(affectedCards);
    }

    /**
     * Restore cards from trash bin or put them in trash bin, depending on their previous status.
     */
    @Transactional
    public void switchCardTrashBinStatus(List<String> ids) {
        List<Card> affectedCards = new ArrayList<>();

        for (String cardId : ids) {
            Card card = find(cardId);
            // either restore from trash bin or put in trash bin
            card.setStatus(card.inTrashBin() ? Card.CardStatus.AVAILABLE : Card.CardStatus.TRASHED);
            affectedCards.add(card);
        }

        store.save(affectedCards);
    }

    public List<CardContentListDto> findAllContentsForCard(String cardId) {
        // verify owner with query into card's index with USER_ID as query param
        Params params = new Params();
        params.setFilter(List.of(new Filter(IndexedCard.ID, FilterOperation.EQ, cardId, null)));
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        Result<CardRef> cardRefs = cardRefStore.findAll(params);
        notEmpty(cardRefs.getItems(), () -> new MissingObject(ENTITY_IS_NULL, Card.class, cardId));

        CardRef cardRef = cardRefs.getItems().get(0);
        return cardContentListDtoStore.listContentsForCard(cardRef.getId());
    }

    @Transactional
    public CardTemplate createTemplateFromCardVersion(String cardContentId) {
        CardContent cardContent = cardContentStore.find(cardContentId);
        notNull(cardContent, () -> new MissingObject(ENTITY_IS_NULL, CardContent.class, cardContentId));

        CardTemplate template = new CardTemplate();
        template.setOwner(cardContent.getCard().getOwner());
        template.setName(cardContent.getCard().getName());
        cardTemplateStore.save(template);
        copyAttributes(cardContent, template);
        return template;
    }

    private void unlinkRelatedEntities(Card card) {
        card.setRecords(Collections.emptySet());
        card.setDocuments(Collections.emptySet());
        card.setCategories(Collections.emptySet());
        card.setLabels(Collections.emptySet());
        card.setComments(Collections.emptyList());
        card.setStructuredNote(null);

        card.setLinkingCards(Collections.emptySet());
        card.setLinkedCards(Collections.emptySet());

        List<CardContent> cardContents = cardContentStore.findCardContentsForCard(card.getId());
        cardContents.forEach(content -> cardContentStore.delete(content));

        store.save(card);
    }

//    private void copyAttributes(CardTemplate source, CardContent target) {
//        Set<Attribute> attributes = new HashSet<>();
//        for (AttributeTemplate template : source.getAttributeTemplates()) {
//            Attribute a = new Attribute();
//            a.setType(template.getType());
//            a.setCardContent(target);
//            a.setName(template.getName());
//            a.setOrdinalNumber(template.getOrdinalNumber());
//            attributes.add(a);
//        }
//        attributeStore.save(attributes);
//        target.getAttributes().addAll(attributes);
//    }

    private void copyAttributes(CardContent source, CardTemplate target) {
        Set<AttributeTemplate> attributeTemplates = new HashSet<>();
        for (Attribute attribute : source.getAttributes()) {
            AttributeTemplate a = new AttributeTemplate();
            a.setType(attribute.getType());
            a.setCardTemplate(target);
            a.setName(attribute.getName());
            a.setOrdinalNumber(attribute.getOrdinalNumber());
            attributeTemplates.add(a);
        }
        attributeTemplateStore.save(attributeTemplates);
        target.getAttributeTemplates().addAll(attributeTemplates);
    }

    @Inject
    public void setStore(CardStore store) {
        this.store = store;
    }

    @Inject
    public void setCardIndex(CardIndexFacade cardIndex) {
        this.cardIndex = cardIndex;
    }

    @Inject
    public void setCardListDtoStore(CardListDtoStore cardListDtoStore) {
        this.cardListDtoStore = cardListDtoStore;
    }

    @Inject
    public void setCardRefStore(CardRefStore cardRefStore) {
        this.cardRefStore = cardRefStore;
    }

    @Inject
    public void setCardContentListDtoStore(CardContentListDtoStore cardContentListDtoStore) {
        this.cardContentListDtoStore = cardContentListDtoStore;
    }

    @Inject
    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    @Inject
    public void setAttributeStore(AttributeStore attributeStore) {
        this.attributeStore = attributeStore;
    }

    @Inject
    public void setCardTemplateStore(CardTemplateStore cardTemplateStore) {
        this.cardTemplateStore = cardTemplateStore;
    }

    @Inject
    public void setCardContentStore(CardContentStore cardContentStore) {
        this.cardContentStore = cardContentStore;
    }

    @Inject
    public void setAttributeTemplateStore(AttributeTemplateStore attributeTemplateStore) {
        this.attributeTemplateStore = attributeTemplateStore;
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
    public void setQuotaVerifier(QuotaVerifier quotaVerifier) {
        this.quotaVerifier = quotaVerifier;
    }

}