package book.store.telegram.strategy.notification.payment;

import book.store.model.Payment;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class SuccessfulPaymentNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Payment> {
    private static final String SUCCESSFUL_PAYMENT = "Successful payment";
    private static final String TELEGRAM = "Telegram";

    @Override
    public void sendMessage(Long chatId, Payment payment) {
        String message = "A payment is paid, payment id: " + payment.getId();
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(SUCCESSFUL_PAYMENT);
    }
}
