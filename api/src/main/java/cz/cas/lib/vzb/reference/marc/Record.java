package cz.cas.lib.vzb.reference.marc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import core.domain.NamedObject;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.dto.CardSimpleConverter;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Main class of module for citations (references) in VZB project.
 * It is lightweight version of its counterpart in open-source MARC4J library.
 * https://github.com/marc4j/marc4j
 * Interfaces in MARC4J e.g. {@link org.marc4j.marc.Record} have already contained getter/setter declaration for id.
 * This is in conflict with {@link core.domain.DomainObject} and therefore this class could not simply implement interface {@link org.marc4j.marc.Record}
 * <p>
 * For visual representation look at javadoc of {@link Record#toString()}
 * <p>
 * If you want to use any MARC4J functionality (e.g. {@link org.marc4j.MarcReader}, {@link org.marc4j.MarcWriter}) then you have to convert this object to its MARC4J counterpart.
 * MARC4J counterpart is {@link org.marc4j.marc.impl.RecordImpl}.
 * <p>
 * This class has following unidirectional relationships
 * {@link Record} --(@OneToMany)--> {@link Datafield} --(@OneToMany)--> {@link Subfield}
 * This class is meant to be used and manipulated on the FE, the others ({@link Datafield}, {@link Subfield}) are not.
 * That's why this class has its own Store {@link RecordStore}, Service {@link RecordService} and CRUD API {@link RecordApi}
 * <p>
 * {@link Datafield} and {@link Subfield} are meant to be part of Record and can not exist individually.
 * Therefore following config of relationship is used
 * <pre>
 *     fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_marc_record")
@Entity
public class Record extends NamedObject {
    @ManyToOne
    @JsonIgnore
    private User owner;

    /**
     * Leader attribute is optional therefore it must be either empty or 24 characters long (MARC21 definition).
     * Setter throws {@link IllegalArgumentException} if this check fails.
     */
    @Size(max = 24)
    private String leader = "";

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "record_id")
    private List<Datafield> dataFields = new ArrayList<>();


    /**
     * Use generated Lombok @Setter with caution. It does not sync {@link Card} side of relationship.
     * For synced work with entities use {@link #addCard(Card)} and {@link #removeRecord(Card)}
     */
    @ManyToMany(mappedBy = "records", fetch = FetchType.EAGER)
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private Set<Card> linkedCards = new HashSet<>();


    public void addCard(Card card) {
        linkedCards.add(requireNonNull(card));
        card.getRecords().add(this);
    }

    public void removeRecord(Card card) {
        linkedCards.remove(requireNonNull(card));
        card.getRecords().remove(this);
    }

    /**
     * Gets a {@link List} of {@link Datafield}s with the supplied tag.
     */
    public List<Datafield> getDataFieldsByTag(String tag) {
        final List<Datafield> result = new ArrayList<Datafield>();

        for (final Datafield field : dataFields) {
            if (field.getTag().equals(tag)) {
                result.add(field);
            }
        }
        return result;
    }


    /**
     * Returns a string representation of this record.
     * <p>
     * Example:
     * <pre>
     *
     *      LEADER 00714cam a2200205 a 4500
     *      020    $a 0786808772
     *      020    $a 0786816155 (pbk.)
     *      040    $a DLC / $c DLC / $d DLC
     *      100 1  $a Chabon, Michael.
     *      245 10 $a Summerland / $c Michael Chabon.
     *      250    $a 1st ed.
     *      260    $a New York / $b Miramax Books/Hyperion Books for Children / $c c2002.
     *      300    $a 500 p. / $c 22 cm.
     *      650  1 $a Fantasy.
     *      650  1 $a Baseball / $vFiction.
     *      650  1 $a Magic/ $v Fiction.
     *
     * </pre>
     *
     * @return String - a string representation of this record
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("LEADER ");
        sb.append(getLeader());
        sb.append(System.lineSeparator());

        for (final Datafield field : dataFields) {
            sb.append(field.toString());
            sb.append(System.lineSeparator());
        }

        return sb.toString();
    }

    public void setLeader(@Nullable String leader) {
        if (leader == null) this.leader = "";
        else if (leader.isEmpty() || leader.length() == 24)
            this.leader = leader;
        else
            throw new IllegalArgumentException("Leader attribute has to be either empty string, null or 24 characters long sequence");

    }

    public Record(String id) {
        this.id = id;
    }

}
