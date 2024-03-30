package book.store.telegram.strategy.notification.book;

import book.store.model.Book;
import book.store.model.Category;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BookUpdatingNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Book> {
    private static final String TELEGRAM = "Telegram";
    private static final String BOOK_UPDATING = "Book updating";

    @Override
    public void sendMessage(Long chatId, Book book) {
        String message = """
                Book was updated.
                
                Now it looks like:
                
                Id: %s,
                Title: %s,
                Author: %s,
                ISBN: %s,
                Description: %s,
                Cover image: %s,
                Categories ids: %s.
                """;
        message = String.format(message,
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getCoverImage(),
                book.getCategories().stream().map(Category::getId).collect(Collectors.toList()));
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(BOOK_UPDATING);
    }
}
