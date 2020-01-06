package cz.cas.lib.vzb;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.ApiTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;

import static core.util.Utils.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminApiTest extends ApiTest {
    @Inject private CardStore cardStore;
    @Inject private UserService userService;
    @Inject protected ObjectMapper objectMapper;

    private User admin = User.builder().password("password").email("mail").allowed(true).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(admin));
    }

    @Test
    public void changeAllowedStatus() throws Exception {
        User disUser1 = User.builder().password("changeToAllowed").email("1mailcz").allowed(false).build();
        User disUser2 = User.builder().password("changeToAllowed").email("2mailcz").allowed(false).build();
        User aUser1 = User.builder().password("disallowThisUser").email("3mailcz").allowed(true).build();
        User aUser2 = User.builder().password("disallowThisUser").email("4mailcz").allowed(true).build();
        transactionTemplate.execute((t) -> {
            userService.create(disUser1);
            userService.create(disUser2);
            userService.create(aUser1);
            userService.create(aUser2);
            return null;
        });

        BulkFlagSetDto allowedUsers = new BulkFlagSetDto();
        allowedUsers.setIds(asList(aUser1.getId(), aUser2.getId()));
        allowedUsers.setValue(Boolean.FALSE);
        String allowedUsersJson = objectMapper.writeValueAsString(allowedUsers);

        BulkFlagSetDto disallowedUsers = new BulkFlagSetDto();
        disallowedUsers.setIds(asList(disUser1.getId(), disUser2.getId()));
        disallowedUsers.setValue(Boolean.TRUE);
        String disallowedUsersJson = objectMapper.writeValueAsString(disallowedUsers);

        //try without login
        securedMvc().perform(
                post("/api/admin/set_allowance")
                        .content(disallowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //try without admin role
        securedMvc().perform(
                post("/api/admin/set_allowance")
                        .content(disallowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //try as admin to set allowed to true
        securedMvc().perform(
                post("/api/admin/set_allowance")
                        .content(disallowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(admin.getId(), Roles.ADMIN)))
                .andExpect(status().isOk());
        assertThat(userService.find(disUser1.getId()).isAllowed(), is(true));
        assertThat(userService.find(disUser2.getId()).isAllowed(), is(true));

        //try as admin to set allowed to false
        securedMvc().perform(
                post("/api/admin/set_allowance")
                        .content(allowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(admin.getId(), Roles.ADMIN)))
                .andExpect(status().isOk());

        assertThat(userService.find(aUser1.getId()).isAllowed(), is(false));
        assertThat(userService.find(aUser2.getId()).isAllowed(), is(false));

    }


    @Test
    public void listUsers() throws Exception {
        User admin2 = User.builder().password("blah").email("mail2").allowed(false).build();
        User user1 = User.builder().password("blah").email("mail3").allowed(true).build();
        User user2 = User.builder().password("blah").email("mail4").allowed(false).build();
        transactionTemplate.execute((t) -> {
            userService.create(admin2);
            userService.create(user1);
            userService.create(user2);
            return null;
        });

        //try without login
        securedMvc().perform(
                get("/api/admin/users")
        )
                .andExpect(status().isForbidden());

        //try without admin role
        securedMvc().perform(
                get("/api/admin/users")
                        .with(mockedUser(admin.getId(), Roles.USER))
        )
                .andExpect(status().isForbidden());

        //try as admin
        securedMvc().perform(
                get("/api/admin/users")
                        .with(mockedUser(admin.getId(), Roles.ADMIN))
                        .param("sorting[0].sort", "email")
                        .param("sorting[0].order", "DESC")
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.items", hasSize(4)))
                .andExpect(jsonPath("$.items[0].id", is(user2.getId())))
                .andExpect(jsonPath("$.items[1].id", is(user1.getId())))
                .andExpect(jsonPath("$.items[2].id", is(admin2.getId())))
                .andExpect(jsonPath("$.items[3].id", is(admin.getId())))
        ;
    }
}
