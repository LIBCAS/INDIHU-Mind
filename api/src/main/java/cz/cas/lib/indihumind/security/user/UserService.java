package cz.cas.lib.indihumind.security.user;

import core.exception.BadArgument;
import core.exception.ConflictObject;
import core.exception.ForbiddenOperation;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.security.authorization.assign.AssignedRoleService;
import core.security.password.GoodPasswordGenerator;
import core.sequence.Sequence;
import core.sequence.SequenceStore;
import core.store.Transactional;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.security.password.PasswordToken;
import cz.cas.lib.indihumind.security.password.PasswordTokenService;
import cz.cas.lib.indihumind.service.MailService;
import cz.cas.lib.indihumind.util.BulkFlagSetDto;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import java.util.*;

import static core.exception.BadArgument.ErrorCode.ARGUMENT_IS_BLANK;
import static core.exception.BadArgument.ErrorCode.WRONG_PASSWORD;
import static core.exception.ConflictObject.ErrorCode.EMAIL_TAKEN;
import static core.exception.ForbiddenOperation.ErrorCode.INVALID_TOKEN;
import static core.exception.ForbiddenOperation.ErrorCode.USER_NOT_LOGGED_IN;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;
import static cz.cas.lib.indihumind.card.CardService.getPidSequenceId;

@Service
public class UserService {

    private UserStore store;
    private PasswordTokenService tokenService;
    private PasswordEncoder passwordEncoder;
    private SequenceStore sequenceStore;
    private GoodPasswordGenerator goodPasswordGenerator;
    private AssignedRoleService assignedRoleService;
    private MailService mailService;
    private UserDelegate userDelegate;

    @Transactional
    public void register(String email, @Nullable String password) {
        User emailTaken = store.findByEmail(email);
        isNull(emailTaken, () -> new ConflictObject(EMAIL_TAKEN, User.class, email));

        if (password == null) password = goodPasswordGenerator.generate();

        User user = new User();
        user.setPassword(password);
        user.setEmail(email);
        if (assignedRoleService.getAssignedRolesMine().contains(Roles.ADMIN))
            user.setAllowed(true);
        create(user);

        mailService.sendUserCreatedEmail(email, password);
    }

    @Transactional
    public User create(@NonNull User user) {
        return createWithRoles(user, Collections.singleton(Roles.USER));
    }

    @Transactional
    public User createWithRoles(@NonNull User user, @NonNull Set<String> roles) {
        // there is no userID primary key constraint in db.changelog so this can be done even before user officially exists in DB
        assignedRoleService.saveAssignedRoles(user.getId(), roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = store.save(user);

        // sequence defines number of cards for a user, used by Card#pid
        Sequence s = new Sequence();
        s.setId(getPidSequenceId(user.getId()));
        s.setFormat("0");
        s.setCounter(1L);
        sequenceStore.save(s);

        return user;
    }


    @Transactional
    public void setAllowedStatus(BulkFlagSetDto dto) {
        List<User> affectedUsers = new ArrayList<>();
        dto.getIds().forEach(userId -> {
            User user = store.find(userId);
            notNull(user, () -> new MissingObject(ENTITY_IS_NULL, User.class, userId));
            user.setAllowed(dto.getValue());
            affectedUsers.add(user);
        });
        store.save(affectedUsers);
    }

    @Transactional
    public void resetPassword(@Email String email) {
        User user = store.findByEmail(email);
        notNull(user, () -> new MissingObject(ENTITY_IS_NULL, User.class, email));

        PasswordToken passwordToken = tokenService.generateNewToken(email);
        mailService.sendResetPasswordEmail(email, passwordToken.getId());
    }

    @Transactional
    public void setNewPassword(String tokenId, String newRawPassword) {
        PasswordToken token = tokenService.find(tokenId);
        eq(Boolean.TRUE, tokenService.isTokenValid(token), () -> new ForbiddenOperation(INVALID_TOKEN, PasswordToken.class, tokenId));
        User user = token.getOwner();
        user.setPassword(passwordEncoder.encode(newRawPassword));
        tokenService.utilizeToken(token);
        store.save(user);
    }

    @Transactional
    public void changePassword(String oldRawPassword, String newRawPassword) {
        User loggedInUser = userDelegate.getUser();
        notNull(loggedInUser, () -> new ForbiddenOperation(USER_NOT_LOGGED_IN));
        ne(Boolean.TRUE, newRawPassword.isBlank(), () -> new BadArgument(ARGUMENT_IS_BLANK, "New password cannot be blank"));

        User user = store.findByEmail(loggedInUser.getEmail());
        notNull(user, () -> new MissingObject(ENTITY_IS_NULL, User.class, loggedInUser.getEmail()));
        notNull(user.getPassword(), () -> new MissingObject(ENTITY_IS_NULL, "Password is null, cannot be changed."));
        eq(Boolean.TRUE, passwordEncoder.matches(oldRawPassword, user.getPassword()), () -> new BadArgument(WRONG_PASSWORD, "Old password does not match with provided."));

        user.setPassword(passwordEncoder.encode(newRawPassword));
        store.save(user);

        mailService.sendPasswordChangedEmail(user.getEmail());
    }

    public Result<User> findAll(Params params) {
        Filter rolesContainsAdmin = new Filter(IndexedUser.ROLES, FilterOperation.CONTAINS, Roles.ADMIN, null);
        addPrefilter(params, new Filter(null, FilterOperation.NEGATE, null, asList(rolesContainsAdmin)));
        return store.findAll(params);
    }

    public void sendTestEmails(String email) {
        mailService.sendDummyEmails(email);
    }

    public Collection<PasswordToken> findAllTokens() {
        return tokenService.listAll();
    }


    @Inject
    public void setStore(UserStore store) {
        this.store = store;
    }

    @Inject
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Inject
    public void setSequenceStore(SequenceStore sequenceStore) {
        this.sequenceStore = sequenceStore;
    }

    @Inject
    public void setGoodPasswordGenerator(GoodPasswordGenerator goodPasswordGenerator) {
        this.goodPasswordGenerator = goodPasswordGenerator;
    }

    @Inject
    public void setAssignedRoleService(AssignedRoleService assignedRoleService) {
        this.assignedRoleService = assignedRoleService;
    }

    @Inject
    public void setMailService(MailService mailService) {
        this.mailService = mailService;
    }

    @Inject
    public void setTokenService(PasswordTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }
}
