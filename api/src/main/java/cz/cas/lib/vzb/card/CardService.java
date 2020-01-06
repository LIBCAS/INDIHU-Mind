package cz.cas.lib.vzb.card;

import core.exception.*;
import core.index.dto.*;
import core.sequence.Generator;
import cz.cas.lib.vzb.card.attachment.AttachmentFileService;
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
import cz.cas.lib.vzb.reference.marc.Record;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.User;
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
    private AttachmentFileService attachmentFileService;


    public CardContent createCard(CreateCardDto cardDto) {
        isNull(store.find(cardDto.getId()), () -> new ConflictObject(CreateCardDto.class, cardDto.getId()));
        User callingUser = userDelegate.getUser();
        Card card = new Card();
        card.setId(cardDto.getId());
        card.setName(cardDto.getName());
        card.setNote(cardDto.getNote());
        card.setCategories(cardDto.getCategories().stream().map(Category::new).collect(Collectors.toSet()));
        card.setLabels(cardDto.getLabels().stream().map(Label::new).collect(Collectors.toSet()));
        card.setRecords(cardDto.getRecords().stream().map(Record::new).collect(Collectors.toSet()));
        card.setLinkedCards(cardDto.getLinkedCards().stream().map(Card::new).collect(Collectors.toSet()));
        card.setPid(generator.generatePlain(getPidSequenceId(callingUser.getId())));
        card.setOwner(callingUser);
        store.save(card);
//        Set<AttachmentFile> savedFiles = attachmentFileService.saveAttachments(cardDto.getId(), new HashSet<>(cardDto.getFiles()));
//        card.setFiles(savedFiles);

        CardContent v1 = new CardContent();
        v1.setLastVersion(true);
        v1.setCard(card);
        cardContentStore.save(v1);

        cardDto.getAttributes().forEach(a -> a.setCardContent(v1));
        attributeStore.save(cardDto.getAttributes());

        log.debug(String.format("Creating and indexing new Card %s for user %s", card.getId(), card.getOwner()));
        store.saveAndIndex(card);

        v1.setAttributes(new HashSet<>(cardDto.getAttributes()));
        return v1;
    }

    public CardContent updateCard(String cardId, UpdateCardDto dto) {
        Card fromDb = store.find(cardId);
        notNull(fromDb, () -> new MissingObject(Card.class, cardId));
        eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Card.class, cardId));
        fromDb.setCategories(dto.getCategories().stream().map(Category::new).collect(Collectors.toSet()));
        fromDb.setLabels(dto.getLabels().stream().map(Label::new).collect(Collectors.toSet()));
        fromDb.setRecords(dto.getRecords().stream().map(Record::new).collect(Collectors.toSet()));
        fromDb.setName(dto.getName());
        fromDb.setLinkedCards(dto.getLinkedCards().stream().map(Card::new).collect(Collectors.toSet()));
        fromDb.setNote(dto.getNote());

        log.debug(String.format("Updating and indexing Card %s for user %s", fromDb.getId(), fromDb.getOwner()));
        store.saveAndIndex(fromDb);

        return cardContentStore.findLastVersionOfCard(cardId);
    }

    public CardContent updateCardContent(String cardId, UpdateCardContentDto dto) {
        Card fromDb = store.find(cardId);
        notNull(fromDb, () -> new MissingObject(Card.class, cardId));
        eq(fromDb.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Card.class, cardId));
        CardContent lastContentVersion = cardContentStore.findLastVersionOfCard(cardId);
        if (dto.isNewVersion()) {
            lastContentVersion.setLastVersion(false);
            cardContentStore.save(lastContentVersion);

            CardContent newContent = new CardContent();
            newContent.setId(UUID.randomUUID().toString());
            newContent.setLastVersion(true);
            newContent.setOrigin(lastContentVersion);
            newContent.setCard(lastContentVersion.getCard());

            dto.getAttributes().forEach(a -> {
                a.setCardContent(newContent);
                //assigns new ids to the attributes of the content
                a.setId(UUID.randomUUID().toString());
            });
            cardContentStore.save(newContent);
            attributeStore.save(dto.getAttributes());
            newContent.setAttributes(new HashSet<>(dto.getAttributes()));
            store.saveAndIndex(fromDb);
            return newContent;
        } else {
            lastContentVersion.getAttributes().stream().filter(a -> !dto.getAttributes().contains(a)).forEach(
                    a -> attributeStore.hardDelete(a)
            );
            dto.getAttributes().forEach(a -> a.setCardContent(lastContentVersion));
            lastContentVersion.setAttributes(new HashSet<>(dto.getAttributes()));
            attributeStore.save(lastContentVersion.getAttributes());
            cardContentStore.save(lastContentVersion);
            store.saveAndIndex(fromDb);
            return lastContentVersion;
        }
    }

    public long deleteSoftDeletedCardsOfUser() {
        User callingUser = userDelegate.getUser();
        List<Card> softDeletedCardsOfUser = store.findSoftDeletedCardsOfUser(callingUser.getId());
        long numberOfRemovedCards = softDeletedCardsOfUser.size();
        softDeletedCardsOfUser.forEach(this::hardDelete);
        return numberOfRemovedCards;
    }

    public CardTemplate createTemplateFromCardVersion(String cardContentId) {
        CardContent cardContent = cardContentStore.find(cardContentId);
        notNull(cardContent, () -> new MissingObject(CardContent.class, cardContentId));

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
        notNull(queryString, () -> new BadArgument("query string cant be empty"));
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
                                            .or(Criteria.where(IndexedCard.ATTACHMENT_FILES).sloppy(queryString, sloppyDistance).boost(externalFileBoost)
                                                    .or(Criteria.where(IndexedCard.ATTRIBUTES).sloppy(queryString, sloppyDistance).boost(attributeContentBoost))))));
        } else {
            cardQuery = phrase(IndexedCard.LABELS, queryString).boost(catAndLabelBoost)
                    .or(phrase(IndexedCard.CATEGORIES, queryString).boost(catAndLabelBoost)
                            .or(phrase(IndexedCard.NAME, queryString).boost(cardNameBoost)
                                    .or(phrase(IndexedCard.NOTE, queryString).boost(cardNoteBoost)
                                            .or(phrase(IndexedCard.ATTACHMENT_FILES, queryString).boost(externalFileBoost)
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

    public Card find(String id) {
        return store.find(id);
    }

    public void hardDelete(Card c) {
        notNull(c.getDeleted(), () -> new ForbiddenOperation(Card.class, c.getId()));
        store.hardDelete(c);
    }


    private void changeSoftDeleteFlag(BulkFlagSetDto dto, Instant nowOrNull) {
        List<Card> affectedCards = new ArrayList<>();
        dto.getIds().forEach(cardId -> {
            Card card = find(cardId);
            notNull(card, () -> new MissingObject(Card.class, cardId));
            eq(card.getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(card));
            card.setDeleted(nowOrNull);
            affectedCards.add(card);
        });
        store.saveAndIndex(affectedCards);
    }

    public void setSoftDelete(BulkFlagSetDto dto) {
        if (dto.getValue()) // soft-delete
            changeSoftDeleteFlag(dto, Instant.now());
        else  // remove soft-delete
            changeSoftDeleteFlag(dto, null);
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
                        highlight.getSnipplets().removeIf(str -> !str.contains("<em>"));
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
    public void setAttachmentFileService(AttachmentFileService attachmentFileService) {
        this.attachmentFileService = attachmentFileService;
    }
}