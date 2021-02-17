package cz.cas.lib.indihumind.card;

import com.querydsl.core.types.dsl.BooleanExpression;
import core.domain.DomainObject;
import core.domain.NamedObject;
import core.exception.GeneralException;
import core.index.IndexField;
import core.index.IndexQueryUtils;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.NamedStore;
import core.util.TemporalConverters;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchStore;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcategory.CategoryStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.cardlabel.LabelStore;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.document.AttachmentFileStore;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static core.index.IndexQueryUtils.buildFilters;


/**
 * Store methods will also return Card entities that are soft-deleted (DomainObject.deleted attribute)
 * To filter them out, add preFilter to API endpoints
 */
@Repository
@Slf4j
public class CardStore extends NamedStore<Card, QCard> implements AdvancedSearchStore<IndexedCard> {

    public CardStore() {
        super(Card.class, QCard.class);
        for (java.lang.reflect.Field field : FieldUtils.getFieldsWithAnnotation(IndexedCard.class, Indexed.class)) {
            if (field.isAnnotationPresent(Dynamic.class))
                continue;
            IndexField solrField = new IndexField(field);
            indexedFields.put(solrField.getFieldName(), solrField);
        }
        IndexQueryUtils.INDEXED_FIELDS_MAP.put(getIndexType(), indexedFields);
    }

    private Map<String, IndexField> indexedFields = new HashMap<>();
    private CardContentStore cardContentStore;
    private SolrClient solrClient;
    private LabelStore labelStore;
    private CategoryStore categoryStore;
    private AttachmentFileStore attachmentFileStore;
    @Getter
    private SolrTemplate template;

    /**
     * Without this @Override, Store would not return soft-deleted Cards.
     */
    @Override
    protected BooleanExpression findWhereExpression() {
        return null;
    }

    /**
     * Query Card and initialize LAZY entities.
     */
    public Card findCardNote(String cardId) {
        Card fetch = query()
                .select(qObject())
                .where(qObject().id.eq(cardId).and(qObject().deleted.isNull()))
                .join(qObject().structuredNote).fetchJoin()
                .fetchFirst();
        detachAll();
        return fetch;
    }

    public Long cardNoteSizeForUser(String userId) {
        QCardNote qCardNote = QCardNote.cardNote;
        QCard qCard = qObject();
        Long fetch = query()
                .select(qCardNote.size.sum())
                .where(qObject().owner.id.eq(userId))
                .from(qCard)
                .innerJoin(qCard.structuredNote, qCardNote)
                .fetchOne();
        if (fetch == null)
            fetch = 0L;
        detachAll();
        return fetch;
    }

    public Long cardNoteSizeForSpecificCard(String cardId) {
        QCardNote qCardNote = QCardNote.cardNote;
        QCard qCard = qObject();

        Long fetch = query()
                .select(qCardNote.size)
                .where(qObject().id.eq(cardId))
                .from(qCard)
                .innerJoin(qCard.structuredNote, qCardNote)
                .fetchOne();

        if (fetch == null)
            fetch = 0L;
        detachAll();
        return fetch;
    }


    public List<Card> findSoftDeletedCardsOfUser(String userId) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().owner.id.eq(userId)
                        .and(qObject().deleted.isNotNull()))
                .fetch();
        detachAll();
        return fetch;
    }

    public List<Card> findCardsOfCategory(Category cat) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().categories.contains(cat))
                .fetch();
        detachAll();
        return fetch;
    }

    public List<Card> findCardsOfLabel(Label label) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().labels.contains(label))
                .fetch();
        detachAll();
        return fetch;
    }

    public List<Card> findCardsOfCitation(Citation citation) {
        List<Card> fetch = query()
                .select(qObject())
                .where(qObject().records.contains(citation))
                .fetch();
        detachAll();
        return fetch;
    }


    private SolrInputDocument toSolrInputDocument(Card card) {
        SolrInputDocument cardDoc = new SolrInputDocument();
        cardDoc.addField(IndexedCard.ID, card.getId());
        cardDoc.addField(IndexedCard.PID, card.getPid());
        cardDoc.addField(IndexQueryUtils.TYPE_FIELD, IndexedCard.CARD_TYPE);
        cardDoc.addField(IndexedCard.CREATED, TemporalConverters.instantToIsoUtcString(card.getCreated()));
        cardDoc.addField(IndexedCard.UPDATED, TemporalConverters.instantToIsoUtcString(card.getUpdated()));
        cardDoc.addField(IndexedCard.DELETED, TemporalConverters.instantToIsoUtcString(card.getDeleted()));
        cardDoc.addField(IndexedCard.USER_ID, card.getOwner().getId());
        Set<Category> filledCategories;
        if (card.getCategories().stream().anyMatch(c -> c.getName() == null))
            filledCategories = new HashSet<>(categoryStore.findAllInList(card.getCategories().stream().map(DomainObject::getId).collect(Collectors.toList())));
        else
            filledCategories = card.getCategories();
        Set<Label> filledLabels;
        if (card.getLabels().stream().anyMatch(c -> c.getName() == null))
            filledLabels = new HashSet<>(labelStore.findAllInList(card.getLabels().stream().map(DomainObject::getId).collect(Collectors.toList())));
        else
            filledLabels = card.getLabels();
        Set<AttachmentFile> filledFiles;
        if (card.getDocuments().stream().anyMatch(c -> c.getName() == null))
            filledFiles = new HashSet<>(attachmentFileStore.findAllInList(card.getLabels().stream().map(DomainObject::getId).collect(Collectors.toList())));
        else
            filledFiles = card.getDocuments();
        cardDoc.addField(IndexedCard.LABELS, filledLabels.stream().map(Label::getName).collect(Collectors.toList()));
        cardDoc.addField(IndexedCard.CATEGORIES, filledCategories.stream().map(Category::getName).collect(Collectors.toList()));
        cardDoc.addField(IndexedCard.CATEGORY_IDS, filledCategories.stream().map(Category::getId).collect(Collectors.toList()));
        cardDoc.addField(IndexedCard.ATTACHMENT_FILES_NAMES, filledFiles.stream().map(AttachmentFile::getName).collect(Collectors.toList()));
        cardDoc.addField(IndexedCard.NAME, card.getName());
        cardDoc.addField(IndexedCard.NOTE, card.getRawNote());

        // TODO: Should we index only last card content?
        List<CardContent> allOfCard = cardContentStore.findAllOfCard(card.getId());
        for (CardContent cardContent : allOfCard) {
            SolrInputDocument contentDoc = new SolrInputDocument();
            contentDoc.addField(IndexQueryUtils.TYPE_FIELD, IndexedCard.CONTENT_TYPE);
            contentDoc.addField(IndexedCard.ID, cardContent.getId());
            contentDoc.addField(IndexedCard.CONTENT_CREATED, TemporalConverters.instantToIsoUtcString(cardContent.getCreated()));
            contentDoc.addField(IndexedCard.CONTENT_UPDATED, TemporalConverters.instantToIsoUtcString(cardContent.getUpdated()));
            contentDoc.addField(IndexedCard.CONTENT_LAST_VERSION, cardContent.isLastVersion());
            for (Attribute attribute : cardContent.getAttributes()) {
                Object value = normalizeAttributeValue(attribute);
                contentDoc.addField(normalizeAttributeName(attribute), value);
                if (attribute.getType() == AttributeType.STRING && cardContent.isLastVersion())
                    cardDoc.addField(IndexedCard.ATTRIBUTES, value);
            }
            cardDoc.addChildDocument(contentDoc);
        }

        return cardDoc;
    }

    private Object normalizeAttributeValue(Attribute attribute) {
        if (attribute.getType() == AttributeType.DATETIME || attribute.getType() == AttributeType.DATE) {
            return TemporalConverters.instantToIsoUtcString((Instant) attribute.getValue());
        }
        return attribute.getValue();

    }

    private String normalizeAttributeName(Attribute attribute) {
        if (attribute.getName() == null || attribute.getName().trim().length() == 0)
            throw new IllegalArgumentException("Attribute must have a name");
        String ascii = Normalizer.normalize(attribute.getName(), Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
        String dashCase = ascii.trim().toLowerCase().replaceAll(" ", "_");
        return dashCase + attribute.getType().getIndexSuffix();
    }

    public Map<String, Long> findCategoryFacets(String ownerId) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(IndexedCard.USER_ID + ":" + ownerId);
        solrQuery.setFacet(true);
        solrQuery.setFacetLimit(4000);
        solrQuery.setFields(IndexedCard.ID);
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetMissing(false);
        solrQuery.addFacetField(IndexedCard.CATEGORY_IDS);
        QueryResponse result;
        try {
            result = solrClient.query(getIndexCollection(), solrQuery);
        } catch (SolrServerException | IOException e) {
            throw new GeneralException(e);
        }
        return result.getFacetField(IndexedCard.CATEGORY_IDS).getValues().stream().collect(Collectors.toMap(FacetField.Count::getName, FacetField.Count::getCount));
    }

    //methods copied from indexed store and those overriding domain store


    public Result<Card> findAll(Params params) {
        SimpleQuery query = new SimpleQuery();
        IndexQueryUtils.initSortingAndPaging(query, params, indexedFields);
        query.addProjectionOnField("id");
        query.addCriteria(typeCriteria());
        if (params.getInternalQuery() != null)
            query.addCriteria(params.getInternalQuery());
        query.addFilterQuery(new SimpleFilterQuery(buildFilters(params, getIndexType(), indexedFields)));
        Page<IndexedCard> page = getTemplate().query(getIndexCollection(), query, getUType());
        List<String> ids = page.getContent().stream().map(IndexedCard::getId).collect(Collectors.toList());
        List<Card> sorted = findAllInList(ids);

        Result<Card> result = new Result<>();
        result.setItems(sorted);
        result.setCount(page.getTotalElements());
        return result;
    }

    public HighlightPage<IndexedCard> search(Params params) {
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions options = new HighlightOptions();
        options.addField(IndexedCard.ATTRIBUTES);
        options.addField(IndexedCard.LABELS);
        options.addField(IndexedCard.CATEGORIES);
        options.addField(IndexedCard.NOTE);
        options.addField(IndexedCard.NAME);
        options.addField(IndexedCard.ATTACHMENT_FILES_NAMES);
        options.addHighlightParameter("f." + IndexedCard.CATEGORIES + ".hl.preserveMulti", "true");
        options.addHighlightParameter("f." + IndexedCard.ATTRIBUTES + ".hl.preserveMulti", "true");
        options.addHighlightParameter("f." + IndexedCard.LABELS + ".hl.preserveMulti", "true");
        options.addHighlightParameter("f." + IndexedCard.ATTACHMENT_FILES_NAMES + ".hl.preserveMulti", "true");
        IndexQueryUtils.initSortingAndPaging(query, params, indexedFields);
        query.setHighlightOptions(options);
        query.addProjectionOnField("id");
        query.addCriteria(typeCriteria());
        if (params.getInternalQuery() != null)
            query.addCriteria(params.getInternalQuery());
        query.addFilterQuery(new SimpleFilterQuery(buildFilters(params, getIndexType(), indexedFields)));
        return getTemplate().queryForHighlightPage(getIndexCollection(), query, getUType());
    }

    @Getter
    public Class<IndexedCard> uType = IndexedCard.class;

    @Getter
    private final String indexType = IndexedCard.CARD_TYPE;

    public boolean supportsChildren() {
        return true;
    }

    public Criteria typeCriteria() {
        return Criteria.where(IndexQueryUtils.TYPE_FIELD).in(getIndexType());
    }

    public Card index(Card card) {
        SolrInputDocument cardDoc = toSolrInputDocument(card);
        getTemplate().saveDocument(getIndexCollection(), cardDoc);
        getTemplate().commit(getIndexCollection());
        return card;
    }

    public Collection<? extends Card> index(Collection<? extends Card> objects) {
        if (objects.isEmpty()) {
            return objects;
        }
        List<SolrInputDocument> indexObjects = objects.stream()
                .map(this::toSolrInputDocument)
                .collect(Collectors.toList());
        getTemplate().saveDocuments(getIndexCollection(), indexObjects);
        getTemplate().commit(getIndexCollection());
        return objects;
    }


    public Card saveWithoutIndex(Card entity) {
        return super.save(entity);
    }

    public Collection<? extends Card> saveWithoutIndex(Collection<? extends Card> entities) {
        return super.save(entities);
    }

    @Override
    public void hardDelete(Card entity) {
        super.hardDelete(entity);
        removeIndex(entity);
    }

    /**
     * Sets deleted field to Instant.now() and saves changes to Solr index.
     *
     * In indexed domain stores, such as {@link core.index.IndexedNamedStore#delete(NamedObject)}, delete operations
     * removes index. However, the index is updated in the {@link CardStore} to allow for querying soft-deleted cards
     * that rely on attribute {@link Card#getDeleted()}.
     *
     * @implNote setting field and committing to DB is done manually instead of super.delete().
     *         This way developer has control over entity and field `deleted` and can simply index entity.
     *         Without access to the JPA updated entity there would be no index action on deleted field because it would
     *         be null.
     */
    @Override
    public void delete(Card entity) {
        if (!entityManager.contains(entity) && entity != null) {
            entity = entityManager.find(type, entity.getId());
        }

        if (entity != null) {
            Instant now = Instant.now();
            entity.setDeleted(now);

            entityManager.merge(entity);

            entityManager.flush();
            detachAll();

            logDeleteEvent(entity);

            if (supportsChildren()) {
                removeIndex(entity);
            }
            index(entity); // usually is index removed, here it is kept for purposes of soft-deletion flag
        }
    }

    @Override
    public Card save(Card entity) {
        entity = super.save(entity);
        if (supportsChildren()) {
            removeIndex(entity);
        }
        return index(entity);
    }

    @Override
    public Collection<? extends Card> save(Collection<? extends Card> entities) {
        super.save(entities);
        if (supportsChildren()) {
            entities.forEach(this::removeIndex);
        }
        return index(entities);
    }

    public void removeIndex(Card obj) {
        getTemplate().deleteByIds(getIndexCollection(), obj.getId());
        getTemplate().commit(getIndexCollection());
        if (supportsChildren()) {
            SolrClient client = getTemplate().getSolrClient();
            try {
                client.deleteByQuery(getIndexCollection(), "_root_:" + obj.getId());
                client.commit(getIndexCollection());
            } catch (SolrServerException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeAllIndexes() {
        log.trace("removing all records from core: " + getIndexCollection());
        SolrClient client = getTemplate().getSolrClient();
        try {
            client.deleteByQuery(getIndexCollection(), "*:*");
            client.commit(getIndexCollection());
        } catch (SolrServerException | IOException e) {
            throw new RuntimeException(e);
        }
        log.trace("successfully removed all records of core: " + getIndexCollection());
    }

    public void reindex() {
        Collection<Card> instances = findAll();
        if (instances.isEmpty())
            return;
        log.debug("reindexing " + instances.size() + " records of core: " + getIndexCollection());
        int counter = 0;
        for (Card instance : instances) {
            index(instance);
            counter++;

            // logging information by 20 indexed cards to show progress
            if (counter % 20 == 0 || counter == instances.size()) {
                log.debug("reindexed " + counter + " records of core: " + getIndexCollection());
            }
        }
        log.trace("reindexed all " + instances.size() + " records of core: " + getIndexCollection());
    }

    public void dropReindex() {
        log.info("drop-reindexing core: " + getIndexCollection());
        removeAllIndexes();
        reindex();
    }

    public String getIndexCollection() {
        SolrDocument document = getUType().getAnnotation(SolrDocument.class);

        if (document == null || document.collection().isEmpty()) {
            throw new GeneralException(String.format("Missing Solr @SolrDocument.collection for class: `%s`", getUType().getSimpleName()));
        }

        return document.collection();
    }

    // ------------------------------------ ADVANCED SEARCH MIXIN ------------------------------------
    @Override
    public SolrTemplate getSolrTemplateForSearch() {
        return getTemplate();
    }

    @Override
    public Criteria getTypeCriteriaForSearch() {
        return typeCriteria();
    }

    @Override
    public Class<IndexedCard> getTypeClassForSearch() {
        return getUType();
    }

    @Override
    public Supplier<IndexedCard> getConstructor() {
        return IndexedCard::new;
    }

    // ------------------------------------ SETTERS ------------------------------------
    @Resource(name = "SchemaSolrTemplate")
    public void setTemplate(SolrTemplate template) {
        this.template = template;
    }

    @Inject
    public void setCardContentStore(CardContentStore cardContentStore) {
        this.cardContentStore = cardContentStore;
    }

    @Inject
    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }

    @Inject
    public void setLabelStore(LabelStore labelStore) {
        this.labelStore = labelStore;
    }

    @Inject
    public void setCategoryStore(CategoryStore categoryStore) {
        this.categoryStore = categoryStore;
    }

    @Inject
    public void setAttachmentFileStore(AttachmentFileStore attachmentFileStore) {
        this.attachmentFileStore = attachmentFileStore;
    }
}
