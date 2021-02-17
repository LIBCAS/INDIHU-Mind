package core.security.basic;

import core.Changed;
import core.audit.AuditLogger;
import core.exception.BadRequestException;
import core.security.authorization.assign.audit.LoginEvent;
import core.security.logging.SiftingAppenderInjector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

@Changed("filter reflects authentication exceptions and translates them to error response codes")
@Slf4j
public class BasicAuthenticationFilter
        extends org.springframework.security.web.authentication.www.BasicAuthenticationFilter {
    private AuditLogger logger;

    public BasicAuthenticationFilter(AuthenticationManager authenticationManager, AuditLogger logger) {
        super(authenticationManager);
        this.logger = logger;
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException {
        SiftingAppenderInjector.injectKey();
        logger.logEvent(new LoginEvent(Instant.now(), extractUsername(request), true));
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        try {
            SiftingAppenderInjector.removeKey();
            logger.logEvent(new LoginEvent(Instant.now(), extractUsername(request), false));
        } catch (BadRequestException e) {
            log.warn(e.toString());
            response.setStatus(400);
            return;
        }

        if (failed instanceof BadCredentialsException) {
            IOUtils.write(BasicAuthFailureCode.BAD_CREDENTIALS.toString(), response.getOutputStream(), StandardCharsets.UTF_8);
            response.setStatus(403);
            return;
        }
        if (failed instanceof LockedException) {
            IOUtils.write(BasicAuthFailureCode.LOCKED.toString(), response.getOutputStream(), StandardCharsets.UTF_8);
            response.setStatus(403);
            return;
        }
        if (failed instanceof DisabledException) {
            IOUtils.write(BasicAuthFailureCode.DISABLED.toString(), response.getOutputStream(), StandardCharsets.UTF_8);
            response.setStatus(403);
            return;
        }
        if (failed instanceof CredentialsExpiredException) {
            IOUtils.write(BasicAuthFailureCode.CREDENTIALS_EXPIRED.toString(), response.getOutputStream(), StandardCharsets.UTF_8);
            response.setStatus(403);
            return;
        }
        if (failed instanceof AccountExpiredException) {
            IOUtils.write(BasicAuthFailureCode.ACCOUNT_EXPIRED.toString(), response.getOutputStream(), StandardCharsets.UTF_8);
            response.setStatus(403);
            return;
        }
        if (failed instanceof UsernameNotFoundException) {
            response.setStatus(404);
            return;
        }
        response.setStatus(500);
    }

    private String extractUsername(HttpServletRequest request) throws IOException {
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Basic ")) {
            throw new BadRequestException("missing basic auth header");
        }

        String[] tokens = extractAndDecodeHeader(header, request);
        assert tokens.length == 2;

        return tokens[0];
    }

    /**
     * Decodes the header into a username and password.
     *
     * @throws BadCredentialsException if the Basic header is not present or is not valid
     *                                 Base64
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request) throws IOException {

        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, getCredentialsCharset(request));

        int delimiter = token.indexOf(":");

        if (delimiter == -1) {
            throw new BadRequestException("Invalid basic authentication token. Format is 'email:password' encoded in Base64.");
        }
        return new String[]{token.substring(0, delimiter), token.substring(delimiter + 1)};
    }
}
