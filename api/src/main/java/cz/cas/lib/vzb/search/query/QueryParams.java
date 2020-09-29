package cz.cas.lib.vzb.search.query;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;


/**
 * Data transfer object for specification of filtering, sorting and paging.
 * User fills data into UI, and these "triples" of "SolrField : FilterType : Value" are then used in query in BE.
 * Logical AND is used between filters.
 *
 * Pagination is specified by number of items to retrieve {@link #pageSize} and the page to start on {@link #pageStart}.
 */
@Getter
@Setter
public class QueryParams {

    /** Initial page. **/
    @NotNull
    private Integer pageStart = 0;

    /** Number of requested instances. (null for disabled pagination) **/
    @Nullable
    private Integer pageSize = 10;

    /** Filter conditions. **/
    @NotEmpty
    private List<@Valid QueryFilter> filters = new ArrayList<>();

}