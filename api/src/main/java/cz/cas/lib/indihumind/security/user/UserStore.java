package cz.cas.lib.indihumind.security.user;

import core.index.IndexedDatedStore;
import core.security.authorization.assign.AssignedRoleStore;
import cz.cas.lib.indihumind.util.Reindexable;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;

@Repository
public class UserStore extends IndexedDatedStore<User, QUser, IndexedUser> implements Reindexable {

    private AssignedRoleStore assignedRoleStore;

    public static final String INDEX_TYPE = "user";

    @Override
    public String getIndexType() {
        return INDEX_TYPE;
    }

    public UserStore() {
        super(User.class, QUser.class, IndexedUser.class);
    }

    public User findByEmail(String email) {
        return query()
                .select(qObject())
                .where(qObject().email.eq(email))
                .fetchOne();
    }

    @Override
    public IndexedUser toIndexObject(User o) {
        IndexedUser indexedUser = super.toIndexObject(o);
        indexedUser.setEmail(o.getEmail());
        indexedUser.setAllowed(o.isAllowed());
        indexedUser.setRoles(new ArrayList<>(assignedRoleStore.findAssignedRoles(o.getId())));
        return indexedUser;
    }

    @Inject
    public void setAssignedRoleStore(AssignedRoleStore assignedRoleStore) {
        this.assignedRoleStore = assignedRoleStore;
    }

    @Override
    public void reindexEverything() {
        dropReindex();
    }

    @Override
    public void removeAllDataFromIndex() {
        removeAllIndexes();
    }
}
