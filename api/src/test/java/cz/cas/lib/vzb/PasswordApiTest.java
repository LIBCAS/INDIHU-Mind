package cz.cas.lib.vzb;

import cz.cas.lib.vzb.security.password.PasswordResetToken;
import cz.cas.lib.vzb.security.password.PasswordResetTokenStore;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordApiTest extends ApiTest {
    @Inject
    private UserService userService;
    @Inject
    private PasswordResetTokenStore tokenStore;
    @Value("${server.url}")
    private String serverUrl;

    private User userWithForgottenPwd = User.builder().password("pwd").email("test@mail.cz").allowed(true).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(userWithForgottenPwd));
    }

    @Test
    public void sendPasswordResetToken() throws Exception {
        // with non-existing email
        securedMvc().perform(
                post("/api/password/forgotten/token/{email}", "nonExistent@email.cz"))
                .andExpect(status().isNotFound());


        // with correct email
        String sentLink = securedMvc().perform(
                post("/api/password/forgotten/token/{email}", userWithForgottenPwd.getEmail()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(tokenStore.findAll().size(), is(1));
        PasswordResetToken token = (PasswordResetToken) tokenStore.findAll().stream().findFirst().get();
        assertThat(token.getOwner().getEmail(), is("test@mail.cz"));
        assertThat(serverUrl + "/resetPassword?token="+token.getId(), is(sentLink));
    }

    @Test
    public void updatePassword() throws Exception {
        String oldPasswordHash = userWithForgottenPwd.getPassword();

        PasswordResetToken usedToken = new PasswordResetToken();
        usedToken.setUtilized(true);
        usedToken.setExpirationTime(Instant.now().plus(666, ChronoUnit.MINUTES));
        usedToken.setOwner(userWithForgottenPwd);

        PasswordResetToken expiredToken = new PasswordResetToken();
        expiredToken.setExpirationTime(Instant.now().minus(1, ChronoUnit.MINUTES));
        expiredToken.setOwner(userWithForgottenPwd);

        PasswordResetToken correctToken = new PasswordResetToken();
        correctToken.setExpirationTime(Instant.now().plus(111, ChronoUnit.MINUTES));
        correctToken.setOwner(userWithForgottenPwd);

        transactionTemplate.execute((t) -> {
            usedToken.setId("usedTokenID");
            expiredToken.setId("expiredTokenID");
            correctToken.setId("correctTokenID");
            tokenStore.save(usedToken);
            tokenStore.save(expiredToken);
            tokenStore.save(correctToken);
            return null;
        });

        // non-existing Token
        securedMvc().perform(
                post("/api/password/forgotten/new/{tokenId}", "non-existing-ID-123")
                    .param("password", "newPasswordToSet"))
                .andExpect(status().isNotFound());

        // already used Token
        securedMvc().perform(
                post("/api/password/forgotten/new/{tokenId}", usedToken.getId())
                        .param("password", "newPasswordToSet"))
                .andExpect(status().isForbidden());

        // time-expired Token
        securedMvc().perform(
                post("/api/password/forgotten/new/{tokenId}", expiredToken.getId())
                        .param("password", "newPasswordToSet"))
                .andExpect(status().isForbidden());

        // correct Token
        securedMvc().perform(
                post("/api/password/forgotten/new/{tokenId}", correctToken.getId())
                        .param("password", "newPasswordToSet"))
                .andExpect(status().isOk());

        PasswordResetToken updatedCorrectToken = tokenStore.find(correctToken.getId());
        assertThat(updatedCorrectToken.isUtilized(), is(true));

        String newPasswordHash = updatedCorrectToken.getOwner().getPassword();
        assertThat(oldPasswordHash, is(not(newPasswordHash)));
    }


}
