package core.security.authorization.assign;

import core.store.DomainStore;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static core.util.Utils.asSet;


@Repository
public class AssignedRoleStore extends DomainStore<AssignedRole, QAssignedRole> {

    public AssignedRoleStore() {
        super(AssignedRole.class, QAssignedRole.class);
    }

    public Set<String> findAssignedRoles(String userId) {
        QAssignedRole qAssignedRole = qObject();

        List<String> roles = query().select(qAssignedRole.role)
                .where(qAssignedRole.userId.eq(userId))
                .fetch();

        detachAll();

        return asSet(roles);
    }

    public void deleteRole(String userId, String role) {
        QAssignedRole qAssignedRole = qObject();

        queryFactory.delete(qAssignedRole)
                .where(qAssignedRole.userId.eq(userId))
                .where(qAssignedRole.role.eq(role))
                .execute();
    }

    public void addRole(String userId, String role) {
        AssignedRole assignedRole = new AssignedRole();
        assignedRole.setUserId(userId);
        assignedRole.setRole(role);

        save(assignedRole);
    }

    public List<String> getUsersWithRole(String role) {
        QAssignedRole qObject = qObject();

        List<String> userIds = query().select(qObject.userId)
                .where(qObject.role.eq(role))
                .fetch();

        detachAll();

        return userIds;
    }
}
