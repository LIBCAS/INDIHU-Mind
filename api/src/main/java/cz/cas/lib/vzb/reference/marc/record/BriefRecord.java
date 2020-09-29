package cz.cas.lib.vzb.reference.marc.record;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 * Unstructured Marc Record; used for quickly copy-pasting data
 */
@JsonPropertyOrder({"id", "created", "updated", "deleted", "name", "linkedCards", "document"})
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_marc_brief_record")
@Entity
public class BriefRecord extends Citation {

    @Transient
    private final CitationType type = CitationType.BRIEF;

    /**
     * Raw content, parsing and displaying is done by FE
     */
    @NotNull
    private String content;

}
