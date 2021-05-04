package cz.cas.lib.indihumind.security.user;

import core.security.UserDetails;
import core.security.UserDetailsService;
import core.security.authorization.assign.AssignedRoleService;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class VzbUserDetailsService implements UserDetailsService {

    private UserStore userStore;
    private AssignedRoleService assignedRoleService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userStore.findByEmail(username);

        Set<String> authorityNames = new HashSet<>();
        if (user != null)
            authorityNames.addAll(assignedRoleService.getAssignedRoles(user.getId()));
        Set<GrantedAuthority> authorities = authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        if (user != null) {
            return new UserDelegate(user, authorities);
        }
        return null;
    }

    @Override
    public UserDetails loadUserById(String id) {
        User user = userStore.find(id);
        if (user == null) return null;

        Set<String> authorityNames = new HashSet<>(assignedRoleService.getAssignedRoles(id));
        Set<GrantedAuthority> authorities = authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return new UserDelegate(user, authorities);
    }

    @Inject
    public void setUserStore(UserStore userStore) {
        this.userStore = userStore;
    }

    @Inject
    public void setAssignedRoleService(AssignedRoleService assignedRoleService) {
        this.assignedRoleService = assignedRoleService;
    }
}
