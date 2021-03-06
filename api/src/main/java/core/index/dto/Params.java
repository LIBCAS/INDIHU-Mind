package core.index.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import core.Changed;
import core.index.IndexedStore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.solr.core.query.Criteria;

import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object for specification of filtering, sorting and paging.
 *
 * <p>
 * Sorting is specified by name of attribute to sort on {@link Params#sort} and ascending or descending order
 * specified by {@link Params#order}.
 * </p>
 * <p>
 * Paging is specified by number of items to retrieve {@link Params#pageSize} and the page to start
 * on {@link Params#page}.
 * </p>
 * <p>
 * Filtering is specified by a {@link List} of filters {@link Params#filter}.
 * {@link IndexedStore} does AND between individual filters.
 * </p>
 */
@Getter
@Setter
@Changed("internal filter not supported")
public class Params {
    /**
     * Attribute name to sort on.
     */
    @NotNull
    protected String sort = "created";

    /**
     * Order of sorting.
     *
     * <p>
     * For possible values see {@link Order}.
     * </p>
     */
    @NotNull
    protected Order order = Order.DESC;

    /**
     * Support for sorting on multiple values, if this is not null, fields sort and order above are ignored.
     */
    protected List<SortSpecification> sorting = new ArrayList<>();

    /**
     * Initial page.
     */
    @NotNull
    protected Integer page = 0;

    /**
     * Number of requested instances.
     * 
     * @implNote Disabled pagination is not supported; use very big number to query everything.
     *           Be wary that is it not best practice.
     */
    protected Integer pageSize = 10;

    /**
     * Logic operation between root filters
     */
    @NotNull
    protected RootFilterOperation operation = RootFilterOperation.AND;

    /**
     * Filter conditions.
     */
    @Valid
    protected List<Filter> filter = new ArrayList<>();

    @JsonIgnore
    @Transient
    protected boolean prefilterAdded = false;

    /**
     * Internal query.
     * <p>
     * Used for additional complicated queries added on backend.
     * </p>
     * <p>
     * This will be used in query field, not filter query. Boost will work.
     * </p>
     */
    @JsonIgnore
    @Transient
    protected Criteria internalQuery;

    public void addFilter(Filter filter) {
        List<Filter> newList = new ArrayList<>(getFilter());
        newList.add(filter);
        setFilter(newList);
    }
}
