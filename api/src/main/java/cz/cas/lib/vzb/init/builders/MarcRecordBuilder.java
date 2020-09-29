package cz.cas.lib.vzb.init.builders;

import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.reference.marc.record.Datafield;
import cz.cas.lib.vzb.reference.marc.record.MarcRecord;
import cz.cas.lib.vzb.security.user.User;

import java.util.List;
import java.util.Set;

import static core.util.Utils.asSet;

public final class MarcRecordBuilder {
    private final MarcRecord marcRecord;

    private MarcRecordBuilder() {
        marcRecord = new MarcRecord();
    }

    public static MarcRecordBuilder builder() {
        return new MarcRecordBuilder();
    }

    public MarcRecordBuilder owner(User owner) {
        marcRecord.setOwner(owner);
        return this;
    }

    public MarcRecordBuilder dataFields(List<Datafield> dataFields) {
        marcRecord.setDataFields(dataFields);
        return this;
    }

    public MarcRecordBuilder linkedCards(Set<Card> linkedCards) {
        marcRecord.setLinkedCards(linkedCards);
        return this;
    }

    public MarcRecordBuilder linkedCards(Card... linkedCards) {
        linkedCards(asSet(linkedCards));
        return this;
    }

    public MarcRecordBuilder document(AttachmentFile... documents) {
        marcRecord.setDocuments(asSet(documents));
        return this;
    }

    public MarcRecordBuilder name(String name) {
        marcRecord.setName(name);
        return this;
    }

    public MarcRecordBuilder id(String id) {
        marcRecord.setId(id);
        return this;
    }

    public MarcRecord build() {
        return marcRecord;
    }
}
