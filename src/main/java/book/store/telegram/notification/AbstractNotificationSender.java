package book.store.telegram.notification;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AbstractNotificationSender {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String MESSAGE_SEPARATOR = "***";
    @Autowired
    private List<NotificationService> notificationServices;

    protected void sendMessage(String notificationService,
                                 Long chatId,
                                 String text) {
        text = MESSAGE_SEPARATOR + LINE_SEPARATOR + text + LINE_SEPARATOR + MESSAGE_SEPARATOR;
        notificationServices
                .stream()
                .filter(service -> service.isApplicable(notificationService))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Can't find a notification service " + notificationService))
                .sendMessage(chatId, text);
    }
}
