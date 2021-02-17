package cz.cas.lib.indihumind.advancedsearch.query;

import core.index.IndexFieldType;
import core.index.IndexQueryUtils;
import cz.cas.lib.indihumind.advancedsearch.searchable.AdvancedSearch;
import lombok.Getter;
import lombok.Setter;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.solr.core.mapping.Indexed;

import static org.apache.solr.client.solrj.beans.DocumentObjectBinder.DEFAULT;


/**
 * Represents one indexed field in Solr Document, provides methods for retrieval of field name and type.
 *
 * This class exists mainly to support and enforce following behavior:
 * <ul>
 * <li>
 * {@link #type} is known when constructing filters using {@link IndexQueryUtils}, so that proper filter is constructed.
 * </li>
 * <li>
 * EQ/SUFFIX/PREFIX queries and sorting is not allowed on fields of type {@link IndexFieldType#TEXT} unless there is a {@link Indexed#copyTo()}
 * attribute with {@link core.index.IndexField#STRING_SUFFIX} (for EQ/SUFFIX/PREFIX queries) / {@link core.index.IndexField#SORT_SUFFIX} (for sort support). This is needed because otherwise EQ/SUFFIX/PREFIX queries does not work well on tokenized field types like ({@link IndexFieldType#TEXT}).
 * </li>
 * <li>
 * If there is a {@link Indexed#copyTo()} annotation with {@link core.index.IndexField#SORT_SUFFIX}, this field is used for sorting instead of the
 * main one. The same applies for {@link Indexed#copyTo()} annotation with {@link core.index.IndexField#STRING_SUFFIX} used for EQ/SUFFIX/PREFIX queries.
 * </li>
 * </ul>
 */
@Getter
@Setter
public class IndexedQueryField {
    private String name;
    private String type;
    private String solrField;


    public IndexedQueryField(java.lang.reflect.Field javaField) {
        Field fieldAnnotation = javaField.getAnnotation(Field.class);
        Indexed indexedAnnotation = javaField.getAnnotation(Indexed.class);
        AdvancedSearch searchAnnotation = javaField.getAnnotation(AdvancedSearch.class);
        this.name = searchAnnotation.czech();
        this.solrField = parseSolrFieldName(indexedAnnotation, fieldAnnotation, javaField.getName());
        this.type = searchAnnotation.type().name();
    }

    private String parseSolrFieldName(Indexed indexedAnnotation, Field fieldAnnotation, String javaFieldName) {
        if (!indexedAnnotation.name().isEmpty()) return indexedAnnotation.name();
        if (!indexedAnnotation.value().isEmpty()) return indexedAnnotation.name();
        if (!fieldAnnotation.value().equals(DEFAULT)) return fieldAnnotation.value();
        return javaFieldName;
    }

}