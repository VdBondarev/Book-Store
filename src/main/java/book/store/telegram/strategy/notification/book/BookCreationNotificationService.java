package book.store.telegram.strategy.notification.book;

import book.store.model.Book;
import book.store.model.Category;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class BookCreationNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Book> {
    private static final String BOOK_CREATION = "Book creation";
    private static final String TELEGRAM = "Telegram";

    @Override
    public void sendMessage(Long chatId, Book book) {
        String message = """
                A new book is created.
                                
                Id: %,
                Title: %s,
                Author: %s,
                ISBN: %S,
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
                && messageType.equalsIgnoreCase(BOOK_CREATION);
    }
}
