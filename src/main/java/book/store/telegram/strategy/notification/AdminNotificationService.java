package book.store.telegram.strategy.notification;

public interface AdminNotificationService<T> {
    void sendMessage(Long chatId, T type);

    boolean isApplicable(String notificationService, String messageType);
}
