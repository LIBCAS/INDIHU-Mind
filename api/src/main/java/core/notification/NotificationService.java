package core.notification;

import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.mail.MailCenter;
import core.security.UserDetails;
import core.security.UserDetailsService;
import core.store.Transactional;
import cz.cas.lib.vzb.security.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static core.exception.ForbiddenObject.ErrorCode.NOT_OWNED_BY_USER;
import static core.exception.MissingObject.ErrorCode.ENTITY_IS_NULL;
import static core.util.Utils.*;

/**
 * Center for working with Notification in UAS.
 */
@Service
public class NotificationService {
    private UserDetails userDetails;

    private NotificationStore store;

    private UserDetailsService detailsService;

//    private AssignedRoleService assignedRoleService;
//
//    private RoleStore roleStore;

    private UserService userService;

    private MailCenter mailCenter;


    /**
     * Creates new notification and send it by email if requested and mail subsystem is active.
     *
     * @param title       Title of the Notification
     * @param description Description of the Notification
     * @param recipientId Recipient Id of the Notification
     * @param flash       One time notification
     * @param emailing    Notification should be also send by email
     */
    @Transactional
    public void createNotification(String title, String description, String recipientId, boolean flash, boolean emailing) {
        notNull(title, () -> new IllegalArgumentException("title"));
        notNull(description, () -> new IllegalArgumentException("description"));
        notNull(recipientId, () -> new IllegalArgumentException("recipientId"));

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setDescription(description);
        notification.setRecipientId(recipientId);
        notification.setFlash(flash);
        notification.setEmailing(emailing);

        notification = this.save(notification);

        if (emailing && mailCenter != null) {
            sendMailNotification(notification);
        }
    }

//    /**
//     * Creates notification for users with specified permission.
//     *
//     * @param title       Title of the notification
//     * @param description Body of the notification
//     * @param permission  Permission the users need to have
//     * @param flash       Is this flash notification
//     * @param emailing    Is this email notification
//     */
//    @Transactional
//    public void createMultiNotificationWithPermission(String title, String description, String permission, boolean flash, boolean emailing) {
//        if (roleStore == null || assignedRoleService == null) {
//            throw new UnsupportedOperationException("Multi notification can be used only if internal role system is used");
//        }
//
//        List<UserDetails> users = detailsService.loadUsersWithPermission(permission);
//
//        users.forEach(user -> createNotification(title, description, user.getId(), flash, emailing));
//    }

//    /**
//     * Creates notification for users with specified role.
//     * <p>
//     * This implementation is dependent on the use of internal role system !
//     *
//     * @param title       Title of the notification
//     * @param description Body of the notification
//     * @param roleId      Role the users needs to be in
//     * @param flash       Is this flash notification
//     * @param emailing    Is this email notification
//     * @deprecated Use createMultiNotificationWithPermission instead
//     */
//    @Transactional
//    public void createMultiNotification(String title, String description, String roleId, boolean flash, boolean emailing) {
//        if (roleStore == null || assignedRoleService == null) {
//            throw new UnsupportedOperationException("Multi notification can be used only if internal role system is used");
//        }
//
//        Role role = roleStore.find(roleId);
//        notNull(role, () -> new MissingObject(Role.class, roleId));
//
//        Collection<String> userIds = assignedRoleService.getUsersWithRole(role);
//
//        userIds.forEach(userId -> createNotification(title, description, userId, flash, emailing));
//    }

    /**
     * Lists own received Notifications
     *
     * @param params Params to adhere to
     * @return Result containing Notifications
     */
    @Transactional
    public Result<Notification> listReceivedNotifications(Params params) {
        Filter ownFilter = new Filter("recipientId", FilterOperation.EQ, userDetails.getId(), null);
        params.setFilter(asList(params.getFilter(), ownFilter));

        return store.findAll(params);
    }

    /**
     * Lists own sent Notifications
     *
     * @param params Params to adhere to
     * @return Result containing Notifications
     */
    @Transactional
    public Result<Notification> listSentNotifications(Params params) {
        Filter ownFilter = new Filter("authorId", FilterOperation.EQ, userDetails.getId(), null);
        params.setFilter(asList(params.getFilter(), ownFilter));

        return store.findAll(params);
    }

    /**
     * Marks the notification as read.
     *
     * @param notificationId Id of the notification
     */
    @Transactional
    public void readNotification(String notificationId) {
        Notification notification = store.find(notificationId);
        notNull(notification, () -> new MissingObject(ENTITY_IS_NULL, Notification.class, notificationId));
        eq(notification.getRecipientId(), userDetails.getId(), () -> new ForbiddenObject(NOT_OWNED_BY_USER, Notification.class, notificationId));

        notification.setRead(true);
        this.save(notification);
    }

    private void sendMailNotification(Notification notification) {
        mailCenter.sendNotification(notification.getRecipientEmail(), notification.getTitle(), notification.getDescription());
    }


    /**
     * Fills user details for author and recipient. Only on new entity.
     *
     * @param dto Entity to save
     * @return saved Form
     */
    public Notification save(Notification dto) {
        Notification old = store.find(dto.getId());

        if (old == null) {
            if (unwrap(userDetails) != null) {
                dto.setAuthorId(userDetails.getId());
                dto.setAuthorName(userDetails.getFullName());
            } else {
                dto.setAuthorId(null);
                dto.setAuthorName(null);
            }

            fillRecipient(asSet(dto));
        } else {
            dto.setAuthorId(old.getAuthorId());
            dto.setAuthorName(old.getAuthorName());
            dto.setRecipientId(old.getRecipientId());
            dto.setRecipientName(old.getRecipientName());
        }

        return store.save(dto);
    }

    private void fillRecipient(Collection<Notification> notifications) {
        Map<String, UserDetails> idToNames = new HashMap<>();

        notifications.stream()
                .filter(notification -> notification.getRecipientId() != null)
                .forEach(notification -> {
                    UserDetails user = idToNames.computeIfAbsent(notification.getRecipientId(),
                            id -> detailsService.loadUserById(id)
                    );

                    notification.setRecipientName(user.getFullName());
                    notification.setRecipientEmail(user.getEmail());
                });
    }

    @Inject
    public void setStore(NotificationStore store) {
        this.store = store;
    }

    @Inject
    public void setDetailsService(UserDetailsService detailsService) {
        this.detailsService = detailsService;
    }

    @Autowired(required = false)
    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    @Autowired(required = false)
    public void setMailCenter(MailCenter mailCenter) {
        this.mailCenter = mailCenter;
    }

    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    //    @Autowired(required = false)
//    public void setRoleStore(RoleStore roleStore) {
//        this.roleStore = roleStore;
//    }
//
//    @Autowired(required = false)
//    public void setAssignedRoleService(AssignedRoleService assignedRoleService) {
//        this.assignedRoleService = assignedRoleService;
//    }
}
