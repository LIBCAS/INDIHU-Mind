package cz.cas.lib.vzb.card;

import core.index.IndexField;
import core.index.IndexFieldType;
import core.index.IndexQueryUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@SolrDocument
public class IndexedCard implements Serializable {

    public static final String ATTRIBUTES = "attributes";
    public static final String ID = "id";
    public static final String PID = "pid";
    public static final String USER_ID = "user_id";
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";
    public static final String DELETED = "deleted";
    public static final String NAME = "name";
    public static final String NOTE = "note";
    public static final String CATEGORIES = "categories";
    public static final String CATEGORY_IDS = "category_ids";
    public static final String LABELS = "labels";
    public static final String ATTACHMENT_FILES = "attachment_files";

    public static final String CONTENT_CREATED = "c_created";
    public static final String CONTENT_UPDATED = "c_updated";
    public static final String CONTENT_LAST_VERSION = "c_last_version";

    public static final String CARD_TYPE = "card";
    public static final String CONTENT_TYPE = "content";

    @Field(value = ID)
    @Indexed(type = IndexFieldType.STRING)
    private String id;

    @Field(value = CREATED)
    @Indexed(type = IndexFieldType.DATE)
    private Date created;

    @Field(value = UPDATED)
    @Indexed(type = IndexFieldType.DATE)
    private Date updated;

    @Field(value = DELETED)
    @Indexed(type = IndexFieldType.DATE)
    private Date deleted;

    @Field(value = PID)
    @Indexed(type = IndexFieldType.LONG)
    private String pid;

    @Field(value = NOTE)
    @Indexed(type = IndexFieldType.TEXT)
    private String note;

    @Field(value = NAME)
    @Indexed(type = IndexFieldType.TEXT, copyTo = {NAME + IndexField.SORT_SUFFIX})
    private String name;

    @Field(value = USER_ID)
    @Indexed(type = IndexFieldType.STRING)
    private String userId;

    @Field(value = CATEGORIES)
    @Indexed(type = IndexFieldType.TEXT)
    private List<String> categories;

    @Field(value = CATEGORY_IDS)
    @Indexed(type = IndexFieldType.STRING)
    private List<String> category_ids;

    @Field(value = ATTRIBUTES)
    @Indexed(type = IndexFieldType.TEXT)
    private List<String> attributes;

    @Field(value = LABELS)
    @Indexed(type = IndexFieldType.TEXT)
    private List<String> labels;

    @Field(value = ATTACHMENT_FILES)
    @Indexed(type = IndexFieldType.TEXT)
    private List<String> attachmentFiles;

    /**
     * {@link #CARD_TYPE for cards parent and {@link #CONTENT_TYPE for content childs}}
     */
    @Field(value = IndexQueryUtils.TYPE_FIELD)
    @Indexed(type = IndexFieldType.STRING)
    private String type;

    /**
     * All fields
     */
    @Field("*")
    @Indexed
    @Dynamic
    private Map<String, Object> fields = new HashMap<>();
}