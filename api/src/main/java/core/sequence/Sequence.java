package core.sequence;

import core.Changed;
import core.domain.DatedObject;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.text.DecimalFormat;

/**
 * Formatted sequence of numbers.
 */
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "uas_sequence")
@Changed("not a dictionary object")
public class Sequence extends DatedObject {
    /**
     * Format of the sequence suitable for {@link DecimalFormat} usage.
     *
     * <p>
     * E.g. ISPV-2016/00 is escaped as ISPV'-2'016/00. For further details about the format
     * see <a href="https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html">DecimalFormat</a>.
     * </p>
     */
    protected String format;

    /**
     * Current value of the counter
     */
    protected Long counter;
}
