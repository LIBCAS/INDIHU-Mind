package cz.cas.lib.indihumind.security.delegate;

import core.security.UserDetails;
import cz.cas.lib.indihumind.security.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;

public class UserDelegate implements UserDetails {
    @Getter
    @Setter
    private User user;

    @Setter
    private Collection<? extends GrantedAuthority> authorities;

    public UserDelegate(User user, Collection<? extends GrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        if (user != null) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    @Override
    public String getFullName() {
        if (user != null) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    @Override
    public String getId() {
        if (user != null) {
            return user.getId();
        } else {
            return null;
        }
    }

    @Override
    public String getEmail() {
        if (user != null) {
            return user.getEmail();
        } else {
            return null;
        }
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        HashSet<GrantedAuthority> allAuthorities = new HashSet<>(authorities);
        return allAuthorities;
    }
}
