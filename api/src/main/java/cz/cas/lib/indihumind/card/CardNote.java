package cz.cas.lib.indihumind.card;

import core.domain.DomainObject;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Note can be huge if user inserts images into his text, therefore it must be LAZY loaded and queried only on demand.
 */
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vzb_card_note")
@Entity
public class CardNote extends DomainObject {

    /**
     * Data in JSON structure which can contain images encoded into string.
     */
    private String data;

    /**
     * Size of data in bytes, used for verifying user's allowed space quota.
     */
    private long size;


    public CardNote(String data) {
        this.data = data == null ? "" : data;
        this.size = IndihuMindUtils.stringByteSize(data);
    }
}
