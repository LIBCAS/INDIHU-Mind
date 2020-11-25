package cz.cas.lib.vzb.card;

import core.exception.BadArgument;
import core.exception.ForbiddenObject;
import core.exception.ForbiddenOperation;
import core.exception.MissingObject;
import core.index.dto.*;
import core.sequence.Generator;
import core.store.Transactional;
import cz.cas.lib.vzb.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.card.attribute.*;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.dto.CardSearchResultDto;
import cz.cas.lib.vzb.card.dto.CreateCardDto;
import cz.cas.lib.vzb.card.dto.UpdateCardContentDto;
import cz.cas.lib.vzb.card.dto.UpdateCardDto;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.template.CardTemplate;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.util.IndihuMindUtils;
import cz.cas.lib.vzb.util.QuotaVerifier;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.ForbiddenOperation.ErrorCode.ARGUMENT_IS_NULL;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.index.IndexQueryUtils.phrase;
import static core.util.Utils.*;

@Slf4j
@Service
public class CardService {
    @Getter
    private CardStore store;
    private Generator generator;
    private AttributeStore attributeStore;
    private CardTemplateStore cardTemplateStore;
    private CardContentStore cardContentStore;
    private AttributeTemplateStore attributeTemplateStore;
    private UserDelegate userDelegate;
    private AttachmentFileStore fileStore;
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
        Card card = store.findCardNote(cardId);
        notNull(card, () -> new MissingObject(ENTITY_IS_NULL, Card.class, cardId));
        eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Card.class, cardId));

        return card.getStructuredNote();
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
        card.setLinkedCards(dto.getLinkedCards().stream().map(Card::new).collect(Collectors.toSet()));
        card.setPid(generator.generatePlain(getPidSequenceId(loggedInUser.getId())));
        card.setRecords(dto.getRecords().stream().map(Citation::new).collect(Collectors.toSet()));
        card.setDocuments(asSet(fileStore.findAllInList(dto.getFiles()))); // TODO add to tests
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

        long newSize = IndihuMindUtils.stringByteSize(dto.getNote());
        long oldSize = store.cardNoteSizeForSpecificCard(fromDb.getId());
        quotaVerifier.verify(newSize - oldSize);

        fromDb.setName(dto.getName());
        fromDb.setStructuredNote(new CardNote(dto.getNote()));
        fromDb.setRawNote(dto.getRawNote());
        fromDb.setCategories(dto.getCategories().stream().map(Category::new).collect(Collectors.toSet()));
        fromDb.setLabels(dto.getLabels().stream().map(Label::new).collect(Collectors.toSet()));
        fromDb.setLinkedCards(dto.getLinkedCards().stream().map(Card::new).collect(Collectors.toSet()));
        fromDb.setRecords(dto.getRecords().stream().map(Citation::new).collect(Collectors.toSet()));
        fromDb.setDocuments(asSet(fileStore.findAllInList(dto.getFiles())));

        if (IndihuMindUtils.isCollectionModified(fromDb.getLinkingCards(), dto.getLinkingCards())) {
            Set<Card> oldLinkingCards = fromDb.getLinkingCards();
            oldLinkingCards.forEach(card -> card.getLinkedCards().remove(fromDb));
            store.save(fromDb.getLinkingCards());

            List<Card> newLinkingCards = store.findAllInList(dto.getLinkingCards());
            newLinkingCards.forEach(linkingCard -> linkingCard.getLinkedCards().add(fromDb));
            store.save(newLinkingCards);

            fromDb.setLinkingCards(dto.getLinkingCards().stream().map(Card::new).collect(Collectors.toSet()));
        }

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
    public long eraseAllCardsFromTrashBin() {
        List<Card> softDeletedCardsOfUser = store.findSoftDeletedCardsOfUser(userDelegate.getUser().getId());
        long numberOfRemovedCards = softDeletedCardsOfUser.size();
        softDeletedCardsOfUser.forEach(card -> store.hardDelete(card));
        return numberOfRemovedCards;
    }


    @Transactional
    public void eraseSingleCardFromTrashBin(String cardId) {
        Card card = find(cardId);
        notNull(card.getDeleted(), () -> new ForbiddenOperation(ForbiddenOperation.ErrorCode.ARGUMENT_IS_NULL, Card.class, cardId)); //fixme change to MissingObject, tell FE

        store.hardDelete(card);
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

    public Result<CardSearchResultDto> simpleSearch(String queryString, String userId, int pageSize, int pageNumber) {
        notNull(queryString, () -> new BadArgument(ARGUMENT_IS_NULL, "query string cant be empty"));
        Criteria cardQuery;
        float cardNameBoost = 4;
        float catAndLabelBoost = 1;
        float cardNoteBoost = 2;
        float attributeContentBoost = 1;
        float externalFileBoost = 1;
        int sloppyDistance = 5;
        queryString = queryString.trim();
        if (queryString.contains(" ")) {
            cardQuery = Criteria.where(IndexedCard.LABELS).sloppy(queryString, sloppyDistance).boost(catAndLabelBoost)
                    .or(Criteria.where(IndexedCard.CATEGORIES).sloppy(queryString, sloppyDistance).boost(catAndLabelBoost)
                            .or(Criteria.where(IndexedCard.NAME).sloppy(queryString, sloppyDistance).boost(cardNameBoost)
                                    .or(Criteria.where(IndexedCard.NOTE).sloppy(queryString, sloppyDistance).boost(cardNoteBoost)
                                            .or(Criteria.where(IndexedCard.ATTACHMENT_FILES_NAMES).sloppy(queryString, sloppyDistance).boost(externalFileBoost)
                                                    .or(Criteria.where(IndexedCard.ATTRIBUTES).sloppy(queryString, sloppyDistance).boost(attributeContentBoost))))));
        } else {
            cardQuery = phrase(IndexedCard.LABELS, queryString).boost(catAndLabelBoost)
                    .or(phrase(IndexedCard.CATEGORIES, queryString).boost(catAndLabelBoost)
                            .or(phrase(IndexedCard.NAME, queryString).boost(cardNameBoost)
                                    .or(phrase(IndexedCard.NOTE, queryString).boost(cardNoteBoost)
                                            .or(phrase(IndexedCard.ATTACHMENT_FILES_NAMES, queryString).boost(externalFileBoost)
                                                    .or(phrase(IndexedCard.ATTRIBUTES, queryString).boost(attributeContentBoost))))));
        }

        Params p = new Params();
        p.setFilter(asList(new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userId, null)));
        p.setPageSize(pageSize);
        p.setPage(pageNumber);
        p.setSorting(asList(new SortSpecification("score", Order.DESC), new SortSpecification(IndexedCard.CREATED, Order.DESC)));
        p.setInternalQuery(cardQuery);
        HighlightPage<IndexedCard> page = store.search(p);
        return cardSearchPostProcess(page);
    }

    public Result<Card> findAll(Params params) {
        return store.findAll(params);
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
     * For single use, Card.note was changed to save json because of text editor
     */
    @Transactional
    public void wipeAllCardNotes() {
        Collection<Card> allCards = store.findAll();
        allCards.forEach(card -> {
            card.setStructuredNote(null);
            card.setRawNote("");
        });
        store.save(allCards);
    }

    private Result<CardSearchResultDto> cardSearchPostProcess(HighlightPage<IndexedCard> page) {
        List<String> ids = page.getContent().stream().map(IndexedCard::getId).collect(Collectors.toList());
        Result<CardSearchResultDto> result = new Result<>();
        result.setItems(new ArrayList<>());
        result.setCount(page.getTotalElements());
        if (ids.isEmpty()) {
            return result;
        }
        List<Card> sorted = store.findAllInList(ids);
        Map<String, List<Attribute>> cardAttributesMap = attributeStore.findStringAttributesOfLastContentOfCards(ids);
        for (Card card : sorted) {
            CardSearchResultDto cardSearchResultDto = new CardSearchResultDto();
            cardSearchResultDto.setCard(card);
            result.getItems().add(cardSearchResultDto);
            for (HighlightEntry<IndexedCard> hlEntry : page.getHighlighted()) {
                if (hlEntry.getEntity().getId().equals(card.getId())) {
                    for (HighlightEntry.Highlight highlight : hlEntry.getHighlights()) {
                        highlight.getSnipplets().removeIf(snippetString -> !snippetString.contains("<em>"));
                        Set<String> snippetsSet = new HashSet<>(highlight.getSnipplets());
                        if (snippetsSet.isEmpty() || !cardAttributesMap.containsKey(card.getId()))
                            continue;
                        if (IndexedCard.ATTRIBUTES.equals(highlight.getField().getName())) {
                            for (String snippet : snippetsSet) {
                                String withoutHighlight = snippet.replaceAll("<em>", "").replaceAll("</em>", "");

                                for (Attribute atr : cardAttributesMap.get(card.getId())) {
                                    if (atr.getValue() != null && ((String) atr.getValue()).contains(withoutHighlight))
                                        cardSearchResultDto.getHighlightedAttributes().add(new AttributeHighlightDto(atr.getId(), atr.getName(), snippet));
                                }
                            }
                        } else {
                            cardSearchResultDto.getHighlightMap().put(highlight.getField().getName(), snippetsSet);
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String getPidSequenceId(String userId) {
        return userId + "#" + "pid";
    }

    public List<CardContent> findAllContentsForCard(String cardId) {
        Card card = find(cardId); // verify owner
        return cardContentStore.findAllOfCard(card.getId());
    }

    @Inject
    public void setStore(CardStore store) {
        this.store = store;
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
    public void setFileStore(AttachmentFileStore fileStore) {
        this.fileStore = fileStore;
    }

    @Inject
    public void setQuotaVerifier(QuotaVerifier quotaVerifier) {
        this.quotaVerifier = quotaVerifier;
    }

}