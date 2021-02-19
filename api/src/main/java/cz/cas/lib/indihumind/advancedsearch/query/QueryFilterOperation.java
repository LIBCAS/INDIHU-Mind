package cz.cas.lib.indihumind.advancedsearch.query;

import org.apache.logging.log4j.util.Strings;
import org.springframework.data.solr.core.query.Criteria;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Filter operation to perform for SolrField and value
 */
public enum QueryFilterOperation {
    /** Equal. ( query  field:value ) **/
    EQ {
        @Override
        public Criteria createQueryCriteria(String field, String value) {
            return Criteria.where(field).is(value);
        }
    },

    /** Not equal. ( query  -field:value ) **/
    NEQ {
        @Override
        public Criteria createQueryCriteria(String field, String value) {
            return Criteria.where(field).is(value).not();
        }
    },

    /** Greater than or equals. ( query  field:[value TO *] ) (range queries) **/
    GTE {
        @Override
        public Criteria createQueryCriteria(String field, String value) {
            return Criteria.where(field).greaterThanEqual(value);
        }
    },

    /** Less than or equals. ( query  field:[* TO value] ) (range queries) **/
    LTE {
        @Override
        public Criteria createQueryCriteria(String field, String value) {
            return Criteria.where(field).lessThanEqual(value);
        }
    },

    CONTAINS {
        @Override
        public Criteria createQueryCriteria(String field, String value) {
            List<Criteria> criterion = Arrays.stream(value.split(" "))
                    .filter(Strings::isNotBlank)
                    .distinct()
                    .map(term -> Criteria.where(field).contains(term))
                    .collect(Collectors.toList());

            Criteria result = null;
            for (Criteria criteria : criterion) {
                if (result == null) result = criteria;
                else result = result.connect().and(criteria);
            }

            return result;
        }
    };

    public abstract Criteria createQueryCriteria(String field, String value);
}

