package book.store.telegram.notification;

public interface NotificationService {
    void sendMessage(Long chatId, String text);

    boolean isApplicable(String notificationService);
}
