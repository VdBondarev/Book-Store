package book.store.telegram.strategy.notification.category;

import book.store.model.Category;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class CategoryCreationNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Category> {
    private static final String TELEGRAM = "Telegram";
    private static final String CATEGORY_CREATION = "Category creation";

    @Override
    public void sendMessage(Long chatId, Category category) {
        String message = """
                A new category is created.
                
                Id: %s,
                Name: %s,
                Description: %s.
                """;
        message = String.format(
                message,
                category.getId(),
                category.getName(),
                category.getDescription());
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(CATEGORY_CREATION);
    }
}
