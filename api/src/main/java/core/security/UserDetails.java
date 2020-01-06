package core.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

/**
 * Extends base spring UserDetails and adds id
 */
public interface UserDetails extends org.springframework.security.core.userdetails.UserDetails {
    default String getId() {
        return getUsername();
    }

    default String getEmail() {
        return null;
    }

    default String getFullName() { return getUsername(); }

    /**
     * In multi-tenant application distinguishes the tenant
     *
     * @return Tenant identifier
     */
    default String getTenantId() {
        return null;
    }

    /**
     * Tests if user has permission.
     *
     * @param permission Permission to test
     * @return does user have permission
     */
    default boolean hasPermission(String permission) {
        Collection<? extends GrantedAuthority> authorities = getAuthorities();

        if (authorities != null) {
            return authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(a -> Objects.equals(a, permission));
        }

        return false;
    }
}
