package cz.cas.lib.indihumind.advancedsearch.query;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import cz.cas.lib.indihumind.advancedsearch.validation.AllowedOperationsValidation;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;


/**
 * DTO for single filter condition.
 *
 * Example JSON:
 * <pre>
 *   {
 *     "field": "name",
 *     "type": "STRING",
 *     "value": "user input",
 *     "operation": "EQ"
 *   }
 * </pre>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonDeserialize(converter = QueryFilterConverter.class)
@AllowedOperationsValidation
public class QueryFilter {

    /** Solr attribute name. Corresponds to {@link IndexedQueryField#getSolrField()} **/
    @NotNull
    @ApiModelProperty(value = "Solr field name", dataType = "String", required = true, example = "name")
    private String field;

    /**
     * Type of field.
     *
     * Value can be formatted depending on this value with {@link QueryType#formatQueryValue}
     * Corresponds to {@link IndexedQueryField#getType()}
     */
    @NotNull
    @ApiModelProperty(value = "Field type", dataType = "QueryType", position = 1, required = true, example = "STRING")
    private QueryType type;

    /** Value used in comparision. **/
    @NotNull
    @ApiModelProperty(value = "Searched value", dataType = "String", position = 2, required = true, example = "karta")
    private String value;

    /** Operation to do. **/
    @NotNull
    @ApiModelProperty(value = "Filter operation type", dataType = "QueryFilterOperation", position = 3, required = true, example = "EQ")
    private QueryFilterOperation operation;

}

