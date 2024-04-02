package book.store.telegram.strategy.response.impl;

import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.repository.OrderRepository;
import book.store.telegram.strategy.response.AdminResponseService;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminGetOrderResponseService implements AdminResponseService {
    private static final String ORDER_REGEX =
            "^(?i)Get info about an order with id:\\s*\\d+$";
    private final OrderRepository orderRepository;

    @Override
    public String getMessage(String text) {
        Long id = getId(text);
        Optional<Order> orderOptional = orderRepository.findByIdWithOrderItems(id);
        if (orderOptional.isEmpty()) {
            return "There is no an order by id " + id;
        }
        Order order = orderOptional.get();
        String message = """
                ***
                Found this order.
                
                Id: %s,
                User id: %s,
                Order date: %s,
                Price: %s,
                Status: %s,
                Shipping address: %s,
                Order items ids: %s.
                ***
                """;
        Set<Long> orderItemIds = order.getOrderItems()
                .stream()
                .map(OrderItem::getId)
                .collect(Collectors.toSet());
        return String.format(
                message,
                order.getId(),
                order.getUserId(),
                order.getOrderDate(),
                order.getPrice(),
                order.getStatus().name(),
                order.getShippingAddress(),
                orderItemIds
        );
    }

    @Override
    public boolean isApplicable(String text) {
        return text.matches(ORDER_REGEX);
    }
}
