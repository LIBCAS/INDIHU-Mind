package core.report;

import core.domain.DatedObject;
import core.file.FileRef;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * Single named report definition.
 */
@Getter
@Setter
@Entity
@Table(name = "uas_report")
public class ReportTemplate extends DatedObject {

    protected String name;

    /**
     * File name for generated report
     */
    protected String fileName;

    /**
     * File template.
     */
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected FileRef template;

}
