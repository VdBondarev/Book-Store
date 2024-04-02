package book.store.service.order;

import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.item.OrderItemResponseDto;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.OrderItemRepository;
import book.store.repository.OrderRepository;
import book.store.repository.ShoppingCartRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(User user, String shippingAddress) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByIdWithCartItemsAndBooks(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user's shopping cart"));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Can't place an order. User didn't add any items to their shopping cart");
        }
        Set<OrderItem> orderItems = shoppingCart.getCartItems()
                .stream()
                .map(item -> {
                    OrderItem orderItem = orderItemMapper.toOrderItem(item);
                    orderItem.setUserId(user.getId());
                    BigDecimal price = item.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    orderItem.setPrice(price);
                    return orderItem;
                })
                .collect(Collectors.toSet());
        BigDecimal totalPrice = BigDecimal.valueOf(
                orderItems
                .stream()
                .map(OrderItem::getPrice)
                .mapToDouble(BigDecimal::doubleValue)
                .sum()
        );
        Order order = new Order()
                .setUserId(user.getId())
                .setOrderDate(LocalDate.now())
                .setPrice(totalPrice)
                .setStatus(Order.Status.PENDING)
                .setShippingAddress(shippingAddress);
        orderRepository.save(order);
        orderItems.forEach(item -> {
            item.setOrder(order);
            orderItemRepository.save(item);
        });
        Set<OrderItemResponseDto> orderItemResponseDtos = orderItems
                .stream()
                .map(orderItemMapper::toResponseDto)
                .collect(Collectors.toSet());
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        responseDto.setOrderItems(orderItemResponseDtos);
        return responseDto;
    }
}
