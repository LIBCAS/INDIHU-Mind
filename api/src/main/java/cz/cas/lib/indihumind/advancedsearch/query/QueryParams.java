package cz.cas.lib.indihumind.advancedsearch.query;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueryParams)) return false;
        QueryParams that = (QueryParams) o;
        return getPageStart().equals(that.getPageStart()) && Objects.equals(getPageSize(), that.getPageSize()) && getFilters().equals(that.getFilters());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPageStart(), getPageSize(), getFilters());
    }
}