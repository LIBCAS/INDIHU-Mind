package cz.cas.lib.indihumind.card;

import core.exception.GeneralException;
import core.index.IndexQueryUtils;
import cz.cas.lib.indihumind.card.dto.CardSearchResultDto;
import cz.cas.lib.indihumind.card.view.CardRef;
import cz.cas.lib.indihumind.card.view.CardRefStore;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeStore;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CardIndexFacade {

    private CardRefStore cardRefStore;
    private AttributeStore attributeStore;
    private SolrClient solrClient;

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
            result = solrClient.query(cardRefStore.getIndexCollection(), solrQuery);
        } catch (SolrServerException | IOException e) {
            throw new GeneralException(e);
        }
        return result.getFacetField(IndexedCard.CATEGORY_IDS).getValues().stream().collect(Collectors.toMap(FacetField.Count::getName, FacetField.Count::getCount));
    }

    /**
     * Query Solr for given string
     *
     * Multiple fields are searched. Highlighting is enabled.
     *
     * @param queryString searched term
     * @param ownerId     logged in user ID
     * @return Solr Highlighting result
     */
    public IndexSearchResult highlightedSearch(String queryString, String ownerId, int page, int pageSize) {
        // TODO: add sloppy search?
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(page);
        solrQuery.setRows(pageSize);

        solrQuery.setQuery(prepareSearchQuery(queryString));
        solrQuery.setFields("id", "name");

        solrQuery.addFilterQuery(constructSimpleQuery(IndexQueryUtils.TYPE_FIELD, cardRefStore.getIndexType()));
        solrQuery.addFilterQuery(constructSimpleQuery(IndexedCard.USER_ID, ownerId));

        solrQuery.setSort("score", SolrQuery.ORDER.desc);
        solrQuery.addSort(IndexedCard.CREATED, SolrQuery.ORDER.desc);

        solrQuery.setHighlight(true);
        solrQuery.setParam("hl.q", constructSimpleQuery(IndexedCard.NAME, queryString));
        solrQuery.setHighlightSimplePre("<em>");
        solrQuery.setHighlightSimplePost("</em>");
        setHighlightFields(solrQuery);
        solrQuery.setHighlightFragsize(0);

        QueryResponse result;
        try {
            result = solrClient.query(cardRefStore.getIndexCollection(), solrQuery);
        } catch (SolrServerException | IOException e) {
            throw new GeneralException(e);
        }
        return new IndexSearchResult(result.getHighlighting());
    }

    private String prepareSearchQuery(String queryString) {
        queryString = queryString.trim();
        String nameQ = constructSimpleQuery(IndexedCard.NAME, queryString);
        String labelNamesQ = constructSimpleQuery(IndexedCard.LABEL_NAMES, queryString);
        String categoryNamesQ = constructSimpleQuery(IndexedCard.CATEGORY_NAMES, queryString);
        String documentNamesQ = constructSimpleQuery(IndexedCard.DOCUMENT_NAMES, queryString);
        String noteQ = constructSimpleQuery(IndexedCard.NOTE, queryString);
        String attributesQ = constructSimpleQuery(IndexedCard.ATTRIBUTES, queryString);

        List<String> queryTerms = List.of(nameQ, labelNamesQ, categoryNamesQ, documentNamesQ, noteQ, attributesQ);
        String joinedQuery = String.join(" OR ", queryTerms);

        return joinedQuery;
    }

    private String constructSimpleQuery(String field, String value) {
        return String.format("%s:\"%s\"", field, value);
    }

    private void setHighlightFields(SolrQuery solrQuery) {
        solrQuery.addHighlightField(IndexedCard.ATTRIBUTES);
        solrQuery.addHighlightField(IndexedCard.LABEL_NAMES);
        solrQuery.addHighlightField(IndexedCard.CATEGORY_NAMES);
        solrQuery.addHighlightField(IndexedCard.NOTE);
        solrQuery.addHighlightField(IndexedCard.NAME);
        solrQuery.addHighlightField(IndexedCard.DOCUMENT_NAMES);
    }


    public class IndexSearchResult {

        /**
         * <pre>
         *   {
         *     "cardId": {
         *       "indexedCardFieldName": ["this is <em>snippet1</em>", "another <em>snippet</em>"],
         *       "indexedCardFieldName2": ["<em>snippet</em>"],
         *     }
         *   }
         * </pre>
         */
        private final Map<String, Map<String, List<String>>> highlighting;

        public IndexSearchResult(Map<String, Map<String, List<String>>> highlighting) {
            this.highlighting = highlighting;
        }

        /**
         * Parse highlighting and create {@link CardSearchResultDto} dtos for every card from highlights
         */
        public List<CardSearchResultDto> transform() {
            List<CardSearchResultDto> result = new ArrayList<>();

            List<String> cardIds = getCardIds();
            if (cardIds.isEmpty()) return result;
            List<CardRef> cards = cardRefStore.findAllInList(cardIds);
            Map<String, List<Attribute>> cardAttributesMap = attributeStore.findStringAttributesOfLastContentOfCards(cardIds);

            for (CardRef card : cards) {
                CardSearchResultDto dto = new CardSearchResultDto(card);
                Map<String, List<String>> cardHighlights = getHighlightingForCard(card.getId());
                if (cardHighlights.isEmpty()) continue;

                addHighlightedElementToDto(dto, cardHighlights, IndexedCard.NAME);
                addHighlightedElementToDto(dto, cardHighlights, IndexedCard.NOTE);
                addHighlightedElementToDto(dto, cardHighlights, IndexedCard.CATEGORY_NAMES);
                addHighlightedElementToDto(dto, cardHighlights, IndexedCard.DOCUMENT_NAMES);
                addHighlightedElementToDto(dto, cardHighlights, IndexedCard.LABEL_NAMES);
                addHighlightedAttributesToDto(dto, cardHighlights.get(IndexedCard.ATTRIBUTES), cardAttributesMap.get(card.getId()));

                result.add(dto);
            }
            return result;
        }

        private List<String> getCardIds() {
            return new ArrayList<>(highlighting.keySet());
        }

        private Map<String, List<String>> getHighlightingForCard(String cardId) {
            return highlighting.getOrDefault(cardId, Collections.emptyMap());
        }

        private void addHighlightedElementToDto(CardSearchResultDto dto,
                                                Map<String, List<String>> cardHighlighting,
                                                String indexedFieldName) {
            List<String> nameHighlightings = cardHighlighting.get(indexedFieldName);
            if (nameHighlightings != null) {
                dto.addHighlightElement(indexedFieldName, nameHighlightings);
            }
        }

        private void addHighlightedAttributesToDto(CardSearchResultDto dto,
                                                   List<String> attributeHighlights,
                                                   List<Attribute> attributesFromDb) {
            if (attributeHighlights == null || attributesFromDb == null ||
                    attributeHighlights.isEmpty() || attributesFromDb.isEmpty()) {
                return;
            }
            // compare attribute value with snippet to find out to which Attribute snippet belongs.
            for (String snippet : attributeHighlights) {
                String withoutHighlight = snippet.replaceAll("<em>", "").replaceAll("</em>", "");
                for (Attribute attribute : attributesFromDb) {
                    if (attribute.getValue() != null && ((String) attribute.getValue()).contains(withoutHighlight)) {
                        // Full attribute is used for creating result dto.
                        dto.addHighlightedAttribute(attribute, snippet);
                    }
                }
            }
        }
    }


    @Inject
    public void setCardRefStore(CardRefStore cardRefStore) {
        this.cardRefStore = cardRefStore;
    }

    @Inject
    public void setAttributeStore(AttributeStore attributeStore) {
        this.attributeStore = attributeStore;
    }

    @Inject
    public void setSolrClient(SolrClient solrClient) {
        this.solrClient = solrClient;
    }
}
