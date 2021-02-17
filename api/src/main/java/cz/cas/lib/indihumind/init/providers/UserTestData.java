package cz.cas.lib.indihumind.init.providers;

import core.sequence.Sequence;
import core.sequence.SequenceStore;
import core.util.Utils;
import cz.cas.lib.indihumind.init.builders.UserBuilder;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.security.user.UserService;
import cz.cas.lib.indihumind.security.user.UserStore;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static core.util.Utils.asSet;

@Getter
@Component
public class UserTestData implements TestDataRemovable {
    public static final String TEST_USER_EMAIL = "user@vzb.cz";
    public static final String ADMIN_USER_EMAIL = "admin@vzb.cz";

    @Inject private UserStore store;
    @Inject private UserService userService;
    @Inject private SequenceStore sequenceStore;


    public User createDataReturnTestUser() {
        User admin = UserBuilder.builder().id("644d3d3e-d5bf-4a86-9a2f-3b54036c2cd9").email(ADMIN_USER_EMAIL).password("vzb").allowed(true).build();
        User indihuUser = UserBuilder.builder().id("48df9e3d-ac55-4536-bd75-31edd91c8a3e").email("indihumind@indihu.cz").password("indihu").allowed(true).build();
        User testUser = UserBuilder.builder().id("57a8ae68-9f3c-4d84-98e5-35e5ea8ec878").email(TEST_USER_EMAIL).password("vzb").allowed(true).build();

        userService.createWithRoles(testUser, asSet(Roles.USER));
        userService.createWithRoles(admin, asSet(Roles.USER, Roles.ADMIN));
        userService.createWithRoles(indihuUser, asSet(Roles.USER, Roles.ADMIN));

        Sequence testUserCardSequence = sequenceStore.find(testUser.getId() + "#pid");
        testUserCardSequence.setCounter(6L); // pid that should new Card obtain
        sequenceStore.save(testUserCardSequence);

        return testUser;
    }

    @Override
    public void wipeAllDatabaseData() {
        store.clearTable();
        sequenceStore.clearTable();
    }
}
