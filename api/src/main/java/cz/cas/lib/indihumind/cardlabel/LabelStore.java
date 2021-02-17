package cz.cas.lib.indihumind.cardlabel;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

import static java.util.Objects.requireNonNull;

@Repository
public class LabelStore extends DomainStore<Label, QLabel> {
    public LabelStore() {
        super(Label.class, QLabel.class);
    }

    /**
     * For constraint check; Must be unique name for owner
     **/
    public Label findEqualNameDifferentId(Label newLabel) {
        if (requireNonNull(newLabel).getName() == null) return null;
        if (requireNonNull(newLabel).getOwner() == null) return null;

        Label entity = query()
                .select(qObject())
                .where(qObject().owner.id.eq(newLabel.getOwner().getId()))
                .where(qObject().name.eq(newLabel.getName()))
                .where(qObject().id.ne(newLabel.getId()))
                .fetchFirst();
        detachAll();
        return entity;
    }
}
