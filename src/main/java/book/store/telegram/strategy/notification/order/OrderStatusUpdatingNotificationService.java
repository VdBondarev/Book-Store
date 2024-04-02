package book.store.telegram.strategy.notification.order;

import book.store.model.Order;
import book.store.telegram.notification.AbstractNotificationSender;
import book.store.telegram.strategy.notification.AdminNotificationService;
import org.springframework.stereotype.Service;

@Service
public class OrderStatusUpdatingNotificationService
        extends AbstractNotificationSender
        implements AdminNotificationService<Order> {
    private static final String ORDER_STATUS_UPDATING = "Order status updating";
    private static final String TELEGRAM = "Telegram";

    @Override
    public void sendMessage(Long chatId, Order order) {
        String message = "Order with id "
                + order.getId()
                + " has got updated status "
                + order.getStatus().name()
                + ".";
        sendMessage(TELEGRAM, chatId, message);
    }

    @Override
    public boolean isApplicable(String notificationService, String messageType) {
        return notificationService.equalsIgnoreCase(TELEGRAM)
                && messageType.equalsIgnoreCase(ORDER_STATUS_UPDATING);
    }
}
