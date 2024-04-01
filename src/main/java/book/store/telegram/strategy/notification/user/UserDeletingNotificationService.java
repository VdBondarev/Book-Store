package book.store.telegram.strategy.notification.user;

import book.store.model.User;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class UserDeletingNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<User> {
    private static final String TELEGRAM = "Telegram";
    private static final String USER_DELETING = "User deleting";

    @Override
    public void sendMessage(Long chatId, User user) {
        String message = "User with id " + user.getId() + " is deleted.";
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(USER_DELETING);
    }
}
