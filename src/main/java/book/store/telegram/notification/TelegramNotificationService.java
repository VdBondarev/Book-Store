package book.store.telegram.notification;

import book.store.model.Order;
import book.store.repository.OrderRepository;
import book.store.telegram.BookStoreTelegramBot;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService implements NotificationService {
    private static final String TELEGRAM = "telegram";
    private final BookStoreTelegramBot telegramBot;
    private final OrderRepository orderRepository;
    @Value("${default.telegram.admin.chat.id}")
    private Long chatId;

    @Override
    public void sendMessage(Long chatId, String text) {
        telegramBot.sendMessage(chatId, text);
    }

    @Override
    public boolean isApplicable(String notificationService) {
        return notificationService.equalsIgnoreCase(TELEGRAM);
    }

    @Scheduled(cron = "0 0 9 * * *")
    private void remindOfNotDeliveringOrders() {
        List<Order> overdueOrders = orderRepository.findAllByStatusAndOrderDate(
                Order.Status.PAID, LocalDate.now());
        if (overdueOrders.isEmpty()) {
            sendMessage(chatId, "No overdue orders today.");
            return;
        }
        overdueOrders
                .stream()
                .map(this::createMessageAboutOrder)
                .forEach(message -> sendMessage(chatId, message));
    }

    private String createMessageAboutOrder(Order order) {
        String message = """               
                This order was made %s and is not being delivered yet.
                
                Order id: %s,
                User id: %s,
                Price: %s,
                Status: %s,
                Shipping address: %s.
                
                To get more info about this order send me a message like that:
                Get info about an order with id: %s.
                """;
        return String.format(
                message,
                order.getOrderDate(),
                order.getId(),
                order.getUserId(),
                order.getPrice(),
                order.getStatus().name(),
                order.getShippingAddress(),
                order.getId()
        );
    }
}
