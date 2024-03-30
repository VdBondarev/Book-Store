package book.store.telegram;

import book.store.telegram.strategy.response.AdminResponseStrategy;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class BookStoreTelegramBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "bondbookstorebot";
    private final Long chatId;
    private final AdminResponseStrategy adminResponseStrategy;

    public BookStoreTelegramBot(
            @Value("${telegram.bot.token}") String botToken,
            @Value("${default.telegram.admin.chat.id}") Long chatId,
            AdminResponseStrategy adminResponseStrategy) {
        super(botToken);
        this.chatId = chatId;
        this.adminResponseStrategy = adminResponseStrategy;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String text = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        if (!Objects.equals(chatId, this.chatId)) {
            sendMessage(chatId, "You are not allowed to interact with this bot.");
            return;
        }
        String message = adminResponseStrategy.getResponseService(text).getMessage(text);
        sendMessage(chatId, message);
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    public void sendMessage(Long chatId, String text) {
        if (chatId == null) {
            // if you do not pass here admin chat's id
            // it will be sent to default.telegram.admin.chat.id
            chatId = this.chatId;
        }
        String chatIdString = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdString, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message " + text);
        }
    }
}
