package book.store.telegram.strategy.notification.book;

import book.store.model.Book;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class BookDeletingNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Book> {
    private static final String TELEGRAM = "Telegram";
    private static final String CAR_DELETING = "Book deleting";

    @Override
    public void sendMessage(Long chatId, Book book) {
        String message = "Book with id " + book.getId() + " is deleted.";
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(CAR_DELETING);
    }
}
