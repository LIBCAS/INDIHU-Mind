package cz.cas.lib.vzb;

import core.util.Utils;
import cz.cas.lib.vzb.init.builders.UserBuilder;
import cz.cas.lib.vzb.security.password.PasswordToken;
import cz.cas.lib.vzb.security.password.PasswordTokenStore;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.ApiTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordApiTest extends ApiTest {

    private static final String API_URL = "/api/password/";

    @Inject private UserService userService;
    @Inject private PasswordTokenStore tokenStore;

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.emptySet();
    }

    private final User userWithForgottenPwd = UserBuilder.builder().password("pwd").email("test@mail.cz").allowed(true).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(userWithForgottenPwd));
    }

    @Test
    public void resetPassword() throws Exception {
        // with non-existing email
        securedMvc().perform(
                put(API_URL + "reset")
                        .param("email", "non.existent@email.cz"))
                .andExpect(status().isNotFound());

        // with correct email
        securedMvc().perform(
                put(API_URL + "reset")
                        .param("email", userWithForgottenPwd.getEmail()))
                .andExpect(status().isOk());

        assertThat(tokenStore.findAll().size()).isEqualTo(1);
        PasswordToken token = tokenStore.findAll().stream().findFirst().get();
        assertThat(token.getOwner().getEmail()).isEqualTo(userWithForgottenPwd.getEmail());
    }

    @Test
    public void updatePassword() throws Exception {
        String oldPasswordHash = userWithForgottenPwd.getPassword();

        PasswordToken usedToken = new PasswordToken();
        usedToken.setId("usedTokenID");
        usedToken.setUtilized(true);
        usedToken.setExpirationTime(Instant.now().plus(666, ChronoUnit.MINUTES));
        usedToken.setOwner(userWithForgottenPwd);

        PasswordToken expiredToken = new PasswordToken();
        expiredToken.setId("expiredTokenID");
        expiredToken.setExpirationTime(Instant.now().minus(1, ChronoUnit.MINUTES));
        expiredToken.setOwner(userWithForgottenPwd);

        PasswordToken correctToken = new PasswordToken();
        correctToken.setId("correctTokenID");
        correctToken.setExpirationTime(Instant.now().plus(111, ChronoUnit.MINUTES));
        correctToken.setOwner(userWithForgottenPwd);

        transactionTemplate.execute(t -> tokenStore.save(Utils.asList(usedToken, expiredToken, correctToken)));

        // non-existing Token
        securedMvc().perform(
                put(API_URL + "new/{tokenId}", "non-existing-ID-123")
                        .param("newPassword", "newPasswordToSet"))
                .andExpect(status().isNotFound());

        // already used Token
        securedMvc().perform(
                put(API_URL + "new/{tokenId}", usedToken.getId())
                        .param("newPassword", "newPasswordToSet"))
                .andExpect(status().isForbidden());

        // time-expired Token
        securedMvc().perform(
                put(API_URL + "new/{tokenId}", expiredToken.getId())
                        .param("newPassword", "newPasswordToSet"))
                .andExpect(status().isForbidden());

        // correct Token
        securedMvc().perform(
                put(API_URL + "new/{tokenId}", correctToken.getId())
                        .param("newPassword", "newPasswordToSet"))
                .andExpect(status().isOk());

        PasswordToken updatedCorrectToken = tokenStore.find(correctToken.getId());
        assertThat(updatedCorrectToken.isUtilized()).isTrue();

        String newPasswordHash = updatedCorrectToken.getOwner().getPassword();
        assertThat(oldPasswordHash).isNotEqualTo(newPasswordHash);
    }

}
