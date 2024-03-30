package book.store.telegram.strategy.notification.user;

import book.store.model.Role;
import book.store.model.User;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class RoleUpdatingNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<User> {
    private static final String TELEGRAM = "telegram";
    private static final String ROLE_UPDATING = "Role updating";

    @Override
    public void sendMessage(Long chatId, User user) {
        String message = "User roles were updated to "
                + user.getRoles()
                .stream()
                .map(Role::getName)
                .map(Role.RoleName::toString)
                .toList()
                + " , user id is "
                + user.getId() + ".";
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(ROLE_UPDATING);
    }
}
