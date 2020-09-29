package cz.cas.lib.vzb.security.user;

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
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.security.password.PasswordToken;
import cz.cas.lib.vzb.security.password.PasswordTokenService;
import cz.cas.lib.vzb.service.MailService;
import lombok.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.Email;
import java.util.*;

import static core.util.Utils.*;
import static cz.cas.lib.vzb.card.CardService.getPidSequenceId;

@Service
public class UserService {

    private UserStore store;
    private PasswordTokenService tokenService;
    private PasswordEncoder passwordEncoder;
    private SequenceStore sequenceStore;
    private GoodPasswordGenerator goodPasswordGenerator;
    private AssignedRoleService assignedRoleService;
    private MailService mailService;

    @Transactional
    public void register(String email, @Nullable String password) {
        User emailTaken = store.findByEmail(email);
        isNull(emailTaken, () -> new ConflictObject(User.class, email));

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
            notNull(user, () -> new MissingObject(User.class, userId));
            user.setAllowed(dto.getValue());
            affectedUsers.add(user);
        });
        store.save(affectedUsers);
    }

    @Transactional
    public void resetPassword(@Email String email) {
        User user = store.findByEmail(email);
        notNull(user, () -> new MissingObject(User.class, email));

        PasswordToken passwordToken = tokenService.generateNewToken(email);
        mailService.sendResetPasswordEmail(email, passwordToken.getId());
    }

    @Transactional
    public void setNewPassword(String tokenId, String newPlainPassword) {
        PasswordToken token = tokenService.find(tokenId);
        eq(Boolean.TRUE, tokenService.isTokenValid(token), () -> new ForbiddenOperation(PasswordToken.class, tokenId));
        User user = token.getOwner();
        user.setPassword(passwordEncoder.encode(newPlainPassword));
        tokenService.utilizeToken(token);
        store.save(user);
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

}
