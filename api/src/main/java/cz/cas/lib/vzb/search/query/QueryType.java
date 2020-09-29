package cz.cas.lib.vzb.search.query;

import core.exception.GeneralException;
import core.index.IndexFieldType;
import lombok.Getter;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.EnumSet;
import java.util.Set;

import static cz.cas.lib.vzb.search.query.QueryFilterOperation.*;

public enum QueryType {

    STRING(IndexFieldType.STRING, EnumSet.of(EQ, NEQ, CONTAINS)) {
        @Override
        public String formatQueryValue(String value) {
            if (value == null || value.isEmpty() || value.trim().isEmpty())
                throw new GeneralException("Query value can't be null, empty or whitespace-only");
            return value.trim();
        }

    },

    DATE(IndexFieldType.DATE, EnumSet.of(EQ, NEQ, GTE, LTE)) {
        @Override
        public String formatQueryValue(String value) {
            try {
                return Instant.parse(value).toString();
            } catch (DateTimeParseException e) {
                throw new GeneralException(String.format("Unable to parse date string value: { %s }. Use ISO-8601 UTC text string such as { 2020-01-19T10:15:30.00Z }", value));
            }
        }

    };

    @Getter private final String schemaType;
    @Getter private final Set<QueryFilterOperation> allowedOperations;

    QueryType(String schemaType, EnumSet<QueryFilterOperation> allowedOperations) {
        this.schemaType = schemaType;
        this.allowedOperations = allowedOperations;
    }

    /**
     * Method transforms string value into string accepted by Solr queries
     *
     * Can be used to enforce Solr requirements for query values (e.g. date query string has to be in full "datetime"
     * ISO-8601 UTC format)
     * Can be used to sanitize input from user to improve Solr query matches.
     * Or simply validate string received from FE.
     *
     * @param value raw input value from FE
     * @return string that can be directly used in solr query
     */
    public abstract String formatQueryValue(String value);

}
