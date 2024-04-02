package book.store.telegram.strategy.response.impl;

import book.store.model.OrderItem;
import book.store.repository.OrderItemRepository;
import book.store.telegram.strategy.response.AdminResponseService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminGetOrderItemResponseService implements AdminResponseService {
    private static final String ORDER_ITEM_REGEX =
            "^(?i)Get info about an order item with id:\\s*\\d+$";
    private final OrderItemRepository orderItemRepository;

    @Override
    public String getMessage(String text) {
        Long id = getId(text);
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(id);
        if (orderItemOptional.isEmpty()) {
            return "There is no an order item by id " + id;
        }
        OrderItem orderItem = orderItemOptional.get();
        String message = """
                ***
                Found this order item.
                
                Id: %s,
                Book id: %s,
                Quantity: %s,
                User id: %s,
                Price: %s.
                """;
        return String.format(
                message,
                orderItem.getId(),
                orderItem.getBookId(),
                orderItem.getQuantity(),
                orderItem.getUserId(),
                orderItem.getPrice()
        );
    }

    @Override
    public boolean isApplicable(String text) {
        return text.matches(ORDER_ITEM_REGEX);
    }
}
