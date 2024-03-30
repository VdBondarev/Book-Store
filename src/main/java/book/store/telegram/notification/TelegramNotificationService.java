package book.store.telegram.notification;

import book.store.telegram.BookStoreTelegramBot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private static final String TELEGRAM = "telegram";
    private final BookStoreTelegramBot telegramBot;

    @Override
    public void sendMessage(Long chatId, String text) {
        telegramBot.sendMessage(chatId, text);
    }

    @Override
    public boolean isApplicable(String notificationService) {
        return notificationService.equalsIgnoreCase(TELEGRAM);
    }
}
