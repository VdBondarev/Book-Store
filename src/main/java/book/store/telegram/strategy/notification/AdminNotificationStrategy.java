package book.store.telegram.strategy.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminNotificationStrategy<T> {
    private final List<AdminNotificationService<T>> notificationServices;

    public AdminNotificationService<T> getNotificationService(
            String notificationService,
            String messageType) {
        return notificationServices
                .stream()
                .filter(service -> service.isApplicable(notificationService, messageType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Can't find a notification service for " + messageType));
    }
}
