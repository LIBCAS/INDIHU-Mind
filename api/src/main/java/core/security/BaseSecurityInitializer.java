package core.security;

import core.Changed;
import core.audit.ApiRequestLoggerFilter;
import core.security.basic.BasicAuthenticationFilter;
import core.security.jwt.JwtFilter;
import core.security.jwt.JwtPostFilter;
import core.security.jwt.JwtTokenProvider;
import core.security.jwt.LoggingByUserFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.inject.Inject;
import javax.servlet.Filter;

/**
 * Configurator for authorization and authentication.
 *
 * <p>
 * Configures JWT secondary authentication and authorization.
 * </p>
 * <p>
 * Developer should extend this class and provide {@link AuthenticationProvider} and {@link OncePerRequestFilter}
 * for primary authentication scheme.
 * </p>
 */
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
@EnableWebSecurity
@Changed("bpm filter removed")
public abstract class BaseSecurityInitializer extends WebSecurityConfigurerAdapter {

    private JwtTokenProvider tokenProvider;

    private String jwtHeaderName;


    protected String[] urlPatterns() {
        return new String[]{"/api/**"};
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http
                .requestMatchers()
                .antMatchers(urlPatterns())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .exceptionHandling().and()
                .headers()
                .cacheControl().and()
                .frameOptions().disable()
                .and()
                .authorizeRequests().anyRequest().permitAll().and();

        Filter[] filters = primarySchemeFilters();
        for (Filter filter : filters) {
            httpSecurity = httpSecurity.addFilterBefore(filter, AnonymousAuthenticationFilter.class);
        }

        httpSecurity = httpSecurity.addFilterBefore(new JwtFilter(jwtHeaderName), AnonymousAuthenticationFilter.class);

        httpSecurity.addFilterAfter(new JwtPostFilter(tokenProvider), FilterSecurityInterceptor.class);
        httpSecurity.addFilterAfter(new LoggingByUserFilter(), FilterSecurityInterceptor.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        AuthenticationProvider[] providers = primaryAuthProviders();
        for (AuthenticationProvider provider : providers) {
            auth = auth.authenticationProvider(provider);
        }

        auth.authenticationProvider(tokenProvider);
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    @ConditionalOnProperty(prefix = "api.log", name = "enabled")
    public ApiRequestLoggerFilter apiRequestLoggerFilter() {
        return new ApiRequestLoggerFilter();
    }

    @Inject
    public void setTokenProvider(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Provides primary auth scheme filters.
     *
     * <p>
     * E.g. {@link BasicAuthenticationFilter}
     * </p>
     *
     * @return Filters
     * @throws Exception Any exception will halt starting
     */
    protected abstract Filter[] primarySchemeFilters() throws Exception;

    /**
     * Provides primary auth scheme providers.
     *
     * <p>
     * E.g. {@link DaoAuthenticationProvider}
     * </p>
     *
     * @return Authentication providers
     * @throws Exception Any exception will halt starting
     */
    protected abstract AuthenticationProvider[] primaryAuthProviders() throws Exception;

    @Inject
    public void setJwtHeaderName(@Value("${security.jwt.header:Authorization}") String jwtHeaderName) {
        this.jwtHeaderName = jwtHeaderName;
    }
}
