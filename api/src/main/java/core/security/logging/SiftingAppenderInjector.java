package core.security.logging;

import core.security.UserDetails;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SiftingAppenderInjector {
    // variable used in logback.xml to differentiate between users and their log folders
    private static final String LOGBACK_USER_ID_VARIABLE = "userid";

    public static void injectKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails user = (UserDetails) authentication.getPrincipal();
            String userId = user.getId();
            if (userId != null) {
                MDC.put(LOGBACK_USER_ID_VARIABLE, userId);
            }
        }
    }

    public static void removeKey() {
        MDC.remove(LOGBACK_USER_ID_VARIABLE);
    }
}
