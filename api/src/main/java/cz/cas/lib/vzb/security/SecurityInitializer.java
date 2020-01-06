package cz.cas.lib.vzb.security;

import core.audit.AuditLogger;
import core.security.BaseSecurityInitializer;
import core.security.basic.BasicAuthenticationFilter;
import core.util.Utils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;

import javax.inject.Inject;
import javax.servlet.Filter;

import static core.util.Utils.asArray;

@Configuration
public class SecurityInitializer extends BaseSecurityInitializer {

    private AuditLogger auditLogger;

    private DbAuthenticationProvider dbAuthenticationProvider;

    @Override
    protected Filter[] primarySchemeFilters() throws Exception {
        BasicAuthenticationFilter filter = new BasicAuthenticationFilter(authenticationManager(), auditLogger);
        return asArray(filter);

    }

    @Override
    protected AuthenticationProvider[] primaryAuthProviders() throws Exception {
        return Utils.<AuthenticationProvider>asArray(dbAuthenticationProvider);
    }

    @Inject
    public void setAuditLogger(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    @Inject
    public void setDbAuthenticationProvider(DbAuthenticationProvider dbAuthenticationProvider) {
        this.dbAuthenticationProvider = dbAuthenticationProvider;
    }
}
