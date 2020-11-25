package cz.cas.lib.vzb.init.builders;

import core.util.Utils;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardNote;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.security.user.User;

import java.util.Set;

/**
 * Generated by Builder Generator plugin.
 * Can be re-generated manually if changes to attributes occur.
 */
public final class CardBuilder {
    private final Card card;

    private CardBuilder() {
        card = new Card();
    }

    public static CardBuilder builder() {
        return new CardBuilder();
    }

    public CardBuilder pid(long pid) {
        card.setPid(pid);
        return this;
    }

    public CardBuilder owner(User owner) {
        card.setOwner(owner);
        return this;
    }

    public CardBuilder note(String note) {
        card.setStructuredNote(new CardNote(note));
        return this;
    }

    public CardBuilder rawNote(String rawNote) {
        card.setRawNote(rawNote);
        return this;
    }

    public CardBuilder categories(Set<Category> categories) {
        card.setCategories(categories);
        return this;
    }

    public CardBuilder categories(Category... categories) {
        return categories(Utils.asSet(categories));
    }

    public CardBuilder labels(Set<Label> labels) {
        card.setLabels(labels);
        return this;
    }

    public CardBuilder labels(Label... labels) {
        return labels(Utils.asSet(labels));
    }

    public CardBuilder linkedCards(Set<Card> linkedCards) {
        card.setLinkedCards(linkedCards);
        return this;
    }

    public CardBuilder linkedCards(Card... linkedCards) {
        return linkedCards(Utils.asSet(linkedCards));
    }

    public CardBuilder linkingCards(Set<Card> linkingCards) {
        card.setLinkingCards(linkingCards);
        return this;
    }

    public CardBuilder linkingCards(Card... linkingCards) {
        return linkingCards(Utils.asSet(linkingCards));
    }

    public CardBuilder files(Set<AttachmentFile> files) {
        card.setDocuments(files);
        return this;
    }

    public CardBuilder files(AttachmentFile... files) {
        return files(Utils.asSet(files));
    }

    public CardBuilder records(Set<Citation> records) {
        card.setRecords(records);
        return this;
    }

    public CardBuilder records(Citation... records) {
        return records(Utils.asSet(records));
    }

    public CardBuilder name(String name) {
        card.setName(name);
        return this;
    }

    public CardBuilder id(String id) {
        card.setId(id);
        return this;
    }

    public Card build() {
        return card;
    }
}
