package cz.cas.lib.indihumind.security.jwt;

import core.security.UserDetails;
import core.security.UserDetailsService;
import core.security.jwt.spi.JwtHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;

import static core.util.Utils.notNull;

@Service
@Slf4j
public class VzbJwtHandler implements JwtHandler {

    private UserDetailsService userDetailsService;

    public UserDetails parseClaims(Map<String, Object> claims) {
        String userId = (String) claims.get("sub");
        UserDetails user = userDetailsService.loadUserById(userId); // fully load user with roles and permissions from DB
        notNull(user, () -> new BadCredentialsException("User not found."));
        return user;
    }

    public Map<String, Object> createClaims(UserDetails delegate) {
        String[] roles = delegate.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);

        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("sub", delegate.getId());
        claims.put("email", delegate.getEmail());
        claims.put("authorities", roles);

        return claims;
    }

    @Inject
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}
