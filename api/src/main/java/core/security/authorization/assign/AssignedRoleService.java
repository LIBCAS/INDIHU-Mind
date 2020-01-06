package core.security.authorization.assign;

import com.google.common.collect.Sets;
import core.Changed;
import core.audit.AuditLogger;
import core.security.UserDetails;
import core.security.authorization.assign.audit.RoleAddEvent;
import core.security.authorization.assign.audit.RoleDelEvent;
import core.store.Transactional;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserStore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static core.util.Utils.unwrap;

@Service
@Changed("role entity replaced with hardcoded string")
public class AssignedRoleService {
    private AssignedRoleStore store;

    private UserStore userStore;

    private AuditLogger logger;

    private UserDetails userDetails;

    @Transactional
    public Set<String> getAssignedRoles(String userId) {
        return store.findAssignedRoles(userId);
    }

    @Transactional
    public Set<String> getAssignedRolesMine() {
        UserDetails unwrapped = unwrap(userDetails);

        if (unwrapped != null) {
            return getAssignedRoles(unwrapped.getId());
        } else {
            return null;
        }
    }

    @Transactional
    public void saveAssignedRoles(String userId, Set<String> newRoles) {
        Set<String> oldRoles = getAssignedRoles(userId);

        Sets.SetView<String> removedRoles = Sets.difference(oldRoles, newRoles);
        Sets.SetView<String> addedRoles = Sets.difference(newRoles, oldRoles);

        removedRoles.forEach(role -> {
            store.deleteRole(userId, role);
            logger.logEvent(new RoleDelEvent(Instant.now(), userId, role, role));
        });

        addedRoles.forEach(role -> {
            store.addRole(userId, role);
            logger.logEvent(new RoleAddEvent(Instant.now(), userId, role, role));
        });
    }

    @Transactional
    public void assignRole(String userId, String roleName) {
        store.addRole(userId, roleName);
        logger.logEvent(new RoleAddEvent(Instant.now(), userId, roleName, roleName));
    }

    public Collection<String> getIdsOfUsersWithRole(String roleName) {
        return store.getUsersWithRole(roleName);
    }

    public Collection<User> getUsersWithRole(String roleName) {
        return userStore.findAllInList(store.getUsersWithRole(roleName));
    }

    @Transactional
    public Set<GrantedAuthority> getAuthorities(String userId) {
        Set<String> roles = getAssignedRoles(userId);

        return roles.stream()
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Inject
    public void setStore(AssignedRoleStore store) {
        this.store = store;
    }

    @Inject
    public void setLogger(AuditLogger logger) {
        this.logger = logger;
    }

    @Inject
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Inject
    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }
}
