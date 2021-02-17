package cz.cas.lib.indihumind.init.builders;

import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citation.Datafield;
import cz.cas.lib.indihumind.document.AttachmentFile;
import cz.cas.lib.indihumind.security.user.User;

import java.util.List;
import java.util.Set;

import static core.util.Utils.asSet;

public final class CitationBuilder {
    private final Citation marcRecord;

    private CitationBuilder() {
        marcRecord = new Citation();
    }

    public static CitationBuilder builder() {
        return new CitationBuilder();
    }

    public CitationBuilder owner(User owner) {
        marcRecord.setOwner(owner);
        return this;
    }

    public CitationBuilder dataFields(List<Datafield> dataFields) {
        marcRecord.setDataFields(dataFields);
        return this;
    }

    public CitationBuilder linkedCards(Set<Card> linkedCards) {
        marcRecord.setLinkedCards(linkedCards);
        return this;
    }

    public CitationBuilder linkedCards(Card... linkedCards) {
        linkedCards(asSet(linkedCards));
        return this;
    }

    public CitationBuilder documents(AttachmentFile... documents) {
        marcRecord.setDocuments(asSet(documents));
        return this;
    }

    public CitationBuilder content(String content) {
        marcRecord.setContent(content);
        return this;
    }

    public CitationBuilder name(String name) {
        marcRecord.setName(name);
        return this;
    }

    public CitationBuilder id(String id) {
        marcRecord.setId(id);
        return this;
    }

    public Citation build() {
        return marcRecord;
    }
}
