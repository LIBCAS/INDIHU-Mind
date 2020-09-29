package cz.cas.lib.vzb;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.index.dto.Order;
import core.index.dto.Params;
import core.index.dto.SortSpecification;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.init.builders.UserBuilder;
import cz.cas.lib.vzb.security.user.*;
import helper.ApiTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Set;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminApiTest extends ApiTest {

    private static final String ADMIN_API_URL = "/api/admin/";

    @Inject private UserService userService;
    @Inject private UserStore userStore;
    @Inject protected ObjectMapper objectMapper;

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.singleton(IndexedUser.class);
    }


    private final User admin = UserBuilder.builder().password("password").email("mail").allowed(true).build();

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.createWithRoles(admin, asSet(Roles.USER, Roles.ADMIN)));
    }

    @Test
    public void changeAllowedStatus() throws Exception {
        User disUser1 = UserBuilder.builder().password("changeToAllowed").email("1mailcz").allowed(false).build();
        User disUser2 = UserBuilder.builder().password("changeToAllowed").email("2mailcz").allowed(false).build();
        User aUser1 = UserBuilder.builder().password("disallowThisUser").email("3mailcz").allowed(true).build();
        User aUser2 = UserBuilder.builder().password("disallowThisUser").email("4mailcz").allowed(true).build();
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
                post(ADMIN_API_URL + "set-allowance")
                        .content(disallowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //try without admin role
        securedMvc().perform(
                post(ADMIN_API_URL + "set-allowance")
                        .content(disallowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //try as admin to set allowed to true
        securedMvc().perform(
                post(ADMIN_API_URL + "set-allowance")
                        .content(disallowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(admin.getId(), Roles.ADMIN)))
                .andExpect(status().isOk());
        assertThat(userStore.find(disUser1.getId()).isAllowed()).isTrue();
        assertThat(userStore.find(disUser2.getId()).isAllowed()).isTrue();

        //try as admin to set allowed to false
        securedMvc().perform(
                post(ADMIN_API_URL + "set-allowance")
                        .content(allowedUsersJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(admin.getId(), Roles.ADMIN)))
                .andExpect(status().isOk());

        assertThat(userStore.find(aUser1.getId()).isAllowed()).isFalse();
        assertThat(userStore.find(aUser2.getId()).isAllowed()).isFalse();
    }


    @Test
    public void listUsers() throws Exception {
        User admin2 = UserBuilder.builder().password("blah").email("mail2").allowed(false).build();
        User user1 = UserBuilder.builder().password("blah").email("mail@user.1").allowed(true).build();
        User user2 = UserBuilder.builder().password("blah").email("mail@user.2").allowed(false).build();


        transactionTemplate.execute((t) -> {
            userService.createWithRoles(admin2, asSet(Roles.USER, Roles.ADMIN));
            userService.createWithRoles(user1, asSet(Roles.USER));
            userService.createWithRoles(user2, asSet(Roles.USER));
            return null;
        });

        Params params = new Params();
        params.setSorting(asList(new SortSpecification("email", Order.DESC)));

        //try without login
        securedMvc().perform(
                post(ADMIN_API_URL + "users")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        //try without admin role
        securedMvc().perform(
                post(ADMIN_API_URL + "users")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(admin.getId(), Roles.USER)))
                .andExpect(status().isForbidden());


        //try as admin
        securedMvc().perform(
                post(ADMIN_API_URL + "users")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(admin.getId(), Roles.ADMIN)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.items", hasSize(2))) // admin should not be able to obtain other admins in response
                .andExpect(jsonPath("$.items[0].id", is(user2.getId())))
                .andExpect(jsonPath("$.items[1].id", is(user1.getId())));
    }

}
