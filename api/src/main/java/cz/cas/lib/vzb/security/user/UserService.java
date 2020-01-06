package cz.cas.lib.vzb.security.user;

import core.exception.ConflictObject;
import core.exception.ForbiddenOperation;
import core.exception.MissingObject;
import core.rest.data.DelegateAdapter;
import core.security.authorization.assign.AssignedRoleService;
import core.security.password.GoodPasswordGenerator;
import core.sequence.Sequence;
import core.sequence.SequenceStore;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.security.password.PasswordResetToken;
import cz.cas.lib.vzb.security.password.PasswordResetTokenStore;
import cz.cas.lib.vzb.service.VzbMailCenter;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static core.util.Utils.isNull;
import static core.util.Utils.notNull;
import static cz.cas.lib.vzb.card.CardService.getPidSequenceId;

@Service
public class UserService implements DelegateAdapter<User> {
    @Getter
    private UserStore delegate;
    private PasswordResetTokenStore passwordTokenDelegate;
    private PasswordEncoder passwordEncoder;
    private SequenceStore sequenceStore;
    private GoodPasswordGenerator goodPasswordGenerator;
    private AssignedRoleService assignedRoleService;
    private VzbMailCenter mailCenter;

    // inject from application.yml
    private long expirationTime;
    private String serverUrl;


    public User register(String email, @Nullable String password) {
        User user = delegate.findByEmail(email);
        isNull(user, () -> new ConflictObject(User.class, email));

        String passwd = password;
        if (passwd == null) passwd = goodPasswordGenerator.generate();

        User u = new User();
        u.setPassword(passwd);
        u.setEmail(email);
        if (assignedRoleService.getAssignedRolesMine().contains(Roles.ADMIN)) u.setAllowed(true);
        create(u);
        assignedRoleService.assignRole(u.getId(), Roles.USER);
        mailCenter.sendUserCreatedNotification(email, passwd);
        return u;
    }

    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = save(user);
        Sequence s = new Sequence();
        s.setId(getPidSequenceId(user.getId()));
        s.setFormat("0");
        s.setCounter(1L);
        sequenceStore.save(s);
        return user;
    }

    public void setAllowedStatus(BulkFlagSetDto dto) {
        List<User> affectedUsers = new ArrayList<>();
        dto.getIds().forEach(userId -> {
            User user = delegate.find(userId);
            notNull(user, () -> new MissingObject(User.class, userId));
            user.setAllowed(dto.getValue());
            affectedUsers.add(user);
        });
        save(affectedUsers);
    }


    public String sendResetLinkToEmail(String email) {
        User user = delegate.findByEmail(email);
        notNull(user, () -> new MissingObject(User.class, email));

        PasswordResetToken token = new PasswordResetToken();
        token.setExpirationTime(Instant.now().plus(expirationTime, ChronoUnit.MINUTES));
        token.setOwner(user);
        passwordTokenDelegate.save(token);

        String pwdGenerationLink = serverUrl + "/resetPassword?token=" + token.getId();
        mailCenter.sendUserPasswordGenerationLink(email, pwdGenerationLink);
        return pwdGenerationLink;
    }

    public void validateTokenSetNewPassword(String tokenId, String newPlainPassword) {
        PasswordResetToken token = passwordTokenDelegate.find(tokenId);
        notNull(token, () -> new MissingObject(PasswordResetToken.class, tokenId));

        Instant expirationTime = token.getExpirationTime();
        if (token.isUtilized() || Instant.now().compareTo(expirationTime) > 0)
            throw new ForbiddenOperation(PasswordResetToken.class, tokenId);

        User user = token.getOwner();
        user.setPassword(passwordEncoder.encode(newPlainPassword));
        save(user);

        token.setUtilized(true);
        passwordTokenDelegate.save(token);
    }

    @Inject
    public void setExpirationTime(@Value("${vzb.token.expirationTime}") String expirationTime) {
        this.expirationTime = Long.parseLong(expirationTime);
    }

    @Inject
    public void setServerUrl(@Value("${server.url}") String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Inject
    public void setDelegate(UserStore delegate) {
        this.delegate = delegate;
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
    public void setMailCenter(VzbMailCenter mailCenter) {
        this.mailCenter = mailCenter;
    }

    @Inject
    public void setPasswordTokenDelegate(PasswordResetTokenStore passwordTokenDelegate) {
        this.passwordTokenDelegate = passwordTokenDelegate;
    }
}
