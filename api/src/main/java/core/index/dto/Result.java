package core.index.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Data transfer object for returning instances with total count.
 *
 * @param <T> Type of instances to hold.
 */
@NoArgsConstructor
@AllArgsConstructor(staticName = "with")
@Getter
@Setter
public class Result<T> {
    /**
     * List of instances.
     */
    protected List<T> items;

    /**
     * Total count of instances in store (possibly satisfying the specified {@link Params} filters.
     */
    protected Long count;

    public static <U> Result<U> with(List<U> items, int count) {
        return Result.with(items, (long) count);
    }
}
