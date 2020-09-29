package cz.cas.lib.vzb.search.searchable;

import cz.cas.lib.vzb.search.query.QueryType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdvancedSearch {

    /** Czech name to display on front-end. **/
    String czech();

    /** Type of field, FE will display filter operations based on this **/
    QueryType type();

}
