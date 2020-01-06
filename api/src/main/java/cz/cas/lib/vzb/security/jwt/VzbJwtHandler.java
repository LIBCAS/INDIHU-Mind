package cz.cas.lib.vzb.security.jwt;

import core.security.UserDetails;
import core.security.jwt.spi.JwtHandler;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VzbJwtHandler implements JwtHandler {
    public UserDetails parseClaims(Map<String, Object> claims) {
        User user = new User();
        user.setId((String) claims.get("sub"));
        user.setEmail((String) claims.get("email"));

        List<String> authorityNames = (List<String>) claims.get("authorities");
        Set<GrantedAuthority> authorities = authorityNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new UserDelegate(user, authorities);
    }

    public Map<String, Object> createClaims(UserDetails userDetails) {

        if (userDetails instanceof UserDelegate) {
            UserDelegate delegate = (UserDelegate) userDetails;
            User user = delegate.getUser();

            String[] roles = userDetails.getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .toArray(String[]::new);

            Map<String, Object> claims = new LinkedHashMap<>();
            claims.put("sub", user.getId());
            claims.put("email", user.getEmail());
            claims.put("authorities", roles);

            return claims;
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
