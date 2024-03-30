package book.store.telegram.strategy.notification.category;

import book.store.model.Category;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class CategoryDeletingNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Category> {
    private static final String TELEGRAM = "Telegram";
    private static final String CATEGORY_DELETING = "Category deleting";

    @Override
    public void sendMessage(Long chatId, Category category) {
        String message = "Category with id " + category.getId() + " was deleted.";
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(CATEGORY_DELETING);
    }
}
