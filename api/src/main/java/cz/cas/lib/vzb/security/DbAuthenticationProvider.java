package cz.cas.lib.vzb.security;

import cz.cas.lib.vzb.security.user.VzbUserDetailsService;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class DbAuthenticationProvider extends DaoAuthenticationProvider {
    @Inject
    public DbAuthenticationProvider(VzbUserDetailsService detailService, PasswordEncoder passwordEncoder) {
        super();
        setUserDetailsService(detailService);
        setPasswordEncoder(passwordEncoder);
    }
}
