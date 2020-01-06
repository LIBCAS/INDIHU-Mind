package cz.cas.lib.vzb.security.user;

import core.index.IndexedDatedStore;
import core.security.authorization.assign.AssignedRoleStore;
import lombok.Getter;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.ArrayList;

@Repository
public class UserStore extends IndexedDatedStore<User, QUser, IndexedUser> {

    private AssignedRoleStore assignedRoleStore;
    @Getter
    private final String indexType = "user";

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
}
