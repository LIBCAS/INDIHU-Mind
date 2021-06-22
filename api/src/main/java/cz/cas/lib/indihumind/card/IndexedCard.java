package cz.cas.lib.indihumind.card;

import core.index.*;
import cz.cas.lib.indihumind.advancedsearch.query.QueryType;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearch;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearchClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Date;
import java.util.List;

import core.index.IndexField;
import core.index.IndexFieldType;

@Getter
@Setter
@SolrDocument(collection = "card")
public class IndexedCard extends IndexedDatedObject {

    public static final String USER_ID = "user_id";
    public static final String PID = "pid";
    public static final String NAME = "name";
    public static final String NOTE = "note";
    public static final String STATUS = "status";
    public static final String DOCUMENT_NAMES = "documents";
    public static final String CATEGORY_NAMES = "categories";
    public static final String CATEGORY_IDS = "category_ids";
    public static final String LABEL_NAMES = "labels";
    public static final String LABEL_IDS = "label_ids";

    public static final String CONTENT_CREATED = "content_created";
    public static final String CONTENT_UPDATED = "content_updated";
    public static final String ATTRIBUTES = "attributes";


    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = PID)
    @Indexed(type = IndexFieldType.LONG)
    private long pid;

    @Field(value = NAME)
    @Indexed(type = IndexFieldType.TEXT, copyTo = {NAME + IndexField.SORT_SUFFIX})
    @AdvancedSearch(czech = "Název", type = QueryType.STRING)
    private String name;

    @Field(value = NOTE)
    @Indexed(type = IndexFieldType.TEXT)
    @AdvancedSearch(czech = "Poznámka", type = QueryType.STRING)
    private String note;

    @Field(value = STATUS)
    @Indexed(type = IndexFieldType.STRING)
    private String status;

    @Field(value = DOCUMENT_NAMES)
    @Indexed(type = IndexFieldType.TEXT)
    private List<String> documents;

    @Field(value = CATEGORY_NAMES)
    @Indexed(type = IndexFieldType.TEXT)
    @AdvancedSearch(czech = "Kategorie", type = QueryType.STRING)
    private List<String> categories;

    @Field(value = CATEGORY_IDS)
    @Indexed(type = IndexFieldType.STRING)
    private List<String> categoryIds;

    @Field(value = LABEL_NAMES)
    @Indexed(type = IndexFieldType.TEXT)
    @AdvancedSearch(czech = "Štítky", type = QueryType.STRING)
    private List<String> labels;

    @Field(value = LABEL_IDS)
    @Indexed(type = IndexFieldType.STRING)
    private List<String> labelIds;

    // -------------- Card Content --------------

    @Field(value = CONTENT_CREATED)
    @Indexed(type = IndexFieldType.DATE)
    protected Date contentCreated;

    @Field(value = CONTENT_UPDATED)
    @Indexed(type = IndexFieldType.DATE)
    protected Date contentUpdated;

    @Field(value = ATTRIBUTES)
    @Indexed(type = IndexFieldType.TEXT)
    @AdvancedSearch(czech = "Atributy", type = QueryType.STRING)
    private List<String> attributes;


}