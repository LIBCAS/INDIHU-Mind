package core.audit;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Universal logger of every API request.
 */
@Getter
@Setter
public class ApiRequestLoggerFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger("Api");

    private static final Marker MARKER = MarkerFactory.getMarker("API");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!isAsyncDispatch(request)) {
            String message = createMessage(request);
            log.info(MARKER, message);
        }

        filterChain.doFilter(request, response);

    }

    private String createMessage(HttpServletRequest request) {
        StringBuilder msg = new StringBuilder();

        String ip = getRemoteIp(request);
        if (StringUtils.hasLength(ip)) {
            msg.append("IP: ").append(ip);
        }

        msg.append(request.getMethod()).append(": ").append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            msg.append('?').append(queryString);
        }

        return msg.toString();
    }

    private String getRemoteIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            return request.getRemoteAddr();
        } else {
            return ipAddress;
        }
    }
}
