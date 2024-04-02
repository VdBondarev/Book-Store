package book.store.service.order;

import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.OrderWithoutOrderItemsResponseDto;
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
import book.store.telegram.strategy.notification.AdminNotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private static final String ORDER_STATUS_UPDATING = "Order status updating";
    private static final String TELEGRAM = "Telegram";
    private final OrderRepository orderRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderItemRepository orderItemRepository;
    private final List<AdminNotificationService<Order>> notificationServices;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(User user, String shippingAddress) {
        checkIfOrderExists(user, Order.Status.PENDING);
        ShoppingCart shoppingCart = getShoppingCart(user);
        Order order = new Order()
                .setUserId(user.getId())
                .setOrderDate(LocalDate.now())
                .setStatus(Order.Status.PENDING)
                .setShippingAddress(shippingAddress);
        Set<OrderItem> orderItems = toOrderItems(shoppingCart, user, order);
        order.setPrice(getTotalPrice(orderItems));
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCartRepository.save(shoppingCart);
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        responseDto.setOrderItems(toOrderItemResponseDtos(orderItems));
        return responseDto;
    }

    @Override
    public void cancel(User user) {
        Order order = orderRepository.findByUserIdAndStatus(
                user.getId(), Order.Status.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find an order to cancel"));
        order.setStatus(Order.Status.CANCELED);
        order.setDeleted(true);
        orderRepository.save(order);
    }

    @Override
    public List<OrderWithoutOrderItemsResponseDto> getHistory(User user, Pageable pageable) {
        return orderRepository.findByUserId(user.getId(), pageable)
                .stream()
                .map(orderMapper::toWithoutOrderItemsDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto getOrder(User user, Long id) {
        Order order = orderRepository.findByUserIdWithOrderItems(
                user.getId(), id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find an order by id " + id));
        Set<OrderItemResponseDto> orderItems =
                toOrderItemResponseDtos(order.getOrderItems());
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        responseDto.setOrderItems(orderItems);
        return responseDto;
    }

    @Override
    public void updateStatus(Long id, String status) {
        Order.Status toSet = Order.Status.fromString(status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find an order by id " + id));
        order.setStatus(toSet);
        orderRepository.save(order);
        notificationServices
                .stream()
                .filter(service -> service.isApplicable(TELEGRAM, ORDER_STATUS_UPDATING))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Can't find a notification service"))
                .sendMessage(null, order);
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    protected void markOverdueOrdersAsCanceled() {
        List<Order> overDueOrders =
                orderRepository.findAllByStatusAndOrderDate(
                        Order.Status.PENDING, LocalDate.now());
        overDueOrders.forEach(order -> order.setStatus(Order.Status.CANCELED));
        orderRepository.deleteAll(overDueOrders);
    }

    private void checkIfOrderExists(User user, Order.Status status) {
        if (orderRepository.findByUserIdAndStatus(user.getId(), status)
                .isPresent()) {
            throw new IllegalArgumentException("""
                    Can't place a new order.
                    User already has a pending one.
                    User should pay for that first (or cancel it).
                    """);
        }
    }

    private Set<OrderItem> toOrderItems(
            ShoppingCart shoppingCart,
            User user,
            Order order) {
        return shoppingCart.getCartItems()
                .stream()
                .map(item -> {
                    OrderItem orderItem = orderItemMapper.toOrderItem(item);
                    orderItem.setUserId(user.getId());
                    BigDecimal price = item.getBook().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                    orderItem.setPrice(price);
                    orderItem.setOrder(order);
                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    private ShoppingCart getShoppingCart(User user) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByIdWithCartItemsAndBooks(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user's shopping cart"));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Can't place an order. User didn't add any items to their shopping cart");
        }
        return shoppingCart;
    }

    private BigDecimal getTotalPrice(Set<OrderItem> orderItems) {
        return BigDecimal.valueOf(
                orderItems
                        .stream()
                        .map(OrderItem::getPrice)
                        .mapToDouble(BigDecimal::doubleValue)
                        .sum()
        );
    }

    private Set<OrderItemResponseDto> toOrderItemResponseDtos(Set<OrderItem> orderItems) {
        return orderItems
                .stream()
                .map(orderItemMapper::toResponseDto)
                .collect(Collectors.toSet());
    }
}
