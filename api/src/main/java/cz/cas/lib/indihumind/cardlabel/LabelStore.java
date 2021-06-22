package cz.cas.lib.indihumind.cardlabel;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public List<Label> findByUser(String userId) {
        List<Label> fetch = query()
                .select(qObject())
                .where(qObject().owner.id.eq(userId))
                .orderBy(qObject().ordinalNumber.asc())
                .fetch();
        detachAll();
        return fetch;
    }

    /**
     * Get assignable ordinal number for a new label
     *
     * @param userId logged in user
     * @return 0 if user has no labels so far
     *         otherwise highest ordinal number of user's label + 1
     */
    public int retrieveNextOrdinalOfLabel(String userId) {
        Label userLastLabel = query()
                .select(qObject())
                .where(qObject().owner.id.eq(userId))
                .orderBy(qObject().ordinalNumber.desc())
                .fetchFirst();

        return userLastLabel == null ? 0 : userLastLabel.getOrdinalNumber() + 1;
    }


}
