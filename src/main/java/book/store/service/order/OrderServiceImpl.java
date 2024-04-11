package book.store.service.order;

import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.OrderWithoutOrderItemsResponseDto;
import book.store.dto.order.item.CreateOrderItemRequestDto;
import book.store.dto.order.item.OrderItemResponseDto;
import book.store.mapper.OrderItemMapper;
import book.store.mapper.OrderMapper;
import book.store.model.Book;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.Payment;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.BookRepository;
import book.store.repository.OrderItemRepository;
import book.store.repository.OrderRepository;
import book.store.repository.PaymentRepository;
import book.store.repository.ShoppingCartRepository;
import book.store.telegram.strategy.notification.AdminNotificationStrategy;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
    private final AdminNotificationStrategy<Order> notificationStrategy;
    private final PaymentRepository paymentRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(User user, String shippingAddress) {
        checkIfOrderExists(user.getId(), Order.Status.PENDING);
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
    @Transactional
    public void cancel(User user) {
        Order order = orderRepository.findByUserIdAndStatus(
                user.getId(), Order.Status.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find an order to cancel"));
        Optional<Payment> paymentOptional =
                paymentRepository.findByUserIdAndStatus(
                        user.getId(), Payment.Status.PENDING);
        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            payment.setStatus(Payment.Status.CANCELED);
            payment.setDeleted(true);
            paymentRepository.save(payment);
        }
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
        sendMessage(TELEGRAM, ORDER_STATUS_UPDATING, null, order);
    }

    @Override
    @Transactional
    public OrderResponseDto add(User user, CreateOrderItemRequestDto requestDto) {
        Book book = bookRepository.findByIdWithoutCategories(
                requestDto.bookId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a book by id " + requestDto.bookId()));
        checkIfPaymentExists(user.getId(), Payment.Status.PENDING);
        Order order = getOrderByStatusAndUserId(Order.Status.PENDING, user.getId());
        Optional<OrderItem> sameItem = getSameItem(order, requestDto.bookId());
        if (sameItem.isPresent()) {
            OrderItem orderItem = sameItem.get();
            orderItem.setQuantity(orderItem.getQuantity() + (long) requestDto.quantity());
            setPrice(orderItem, book);
        } else {
            OrderItem orderItem = orderItemMapper.toOrderItem(requestDto);
            setPrice(orderItem, book);
            orderItem.setOrder(order);
            orderItem.setUserId(user.getId());
            order.getOrderItems().add(orderItem);
            orderItemRepository.save(orderItem);
        }
        order.setPrice(getTotalPrice(order.getOrderItems()));
        orderRepository.save(order);
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        responseDto.setOrderItems(
                toOrderItemResponseDtos(
                        order.getOrderItems())
        );
        return responseDto;
    }

    @Override
    public OrderResponseDto removeBookFromOrder(User user, Long bookId) {
        checkIfPaymentExists(user.getId(), Payment.Status.PENDING);
        Order order = getOrderByStatusAndUserId(Order.Status.PENDING, user.getId());
        order.setOrderItems(removeItem(order, bookId));
        if (order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("""
                    Can't remove all order items from the order.
                    User should cancel it or pay for it
                    """);
        }
        order.setPrice(getTotalPrice(order.getOrderItems()));
        orderRepository.save(order);
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        responseDto.setOrderItems(
                toOrderItemResponseDtos(
                        order.getOrderItems()
                )
        );
        return responseDto;
    }

    @Override
    public OrderResponseDto getPending(User user) {
        Order order = getOrderByStatusAndUserId(Order.Status.PENDING, user.getId());
        OrderResponseDto responseDto = orderMapper.toResponseDto(order);
        responseDto.setOrderItems(
                toOrderItemResponseDtos(
                        order.getOrderItems()
                )
        );
        return responseDto;
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void markOverdueOrdersAsCanceled() {
        List<Order> overDueOrders =
                orderRepository.findAllByStatusAndOrderDate(
                        Order.Status.PENDING, LocalDate.now());
        overDueOrders.forEach(order -> order.setStatus(Order.Status.CANCELED));
        orderRepository.deleteAll(overDueOrders);
    }

    private void checkIfOrderExists(Long userId, Order.Status status) {
        if (orderRepository.findByUserIdAndStatus(userId, status)
                .isPresent()) {
            throw new IllegalArgumentException("""
                    Can't place a new order.
                    User already has a pending one.
                    User should pay for that first (or cancel it).
                    """);
        }
    }

    private void checkIfPaymentExists(Long userId, Payment.Status status) {
        if (paymentRepository.findByUserIdAndStatus(
                        userId, status)
                .isPresent()) {
            throw new IllegalArgumentException("""
                    Can't add a new book to an order, because a pending payment exists.
                    User should pay for that first or cancel it
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

    private Optional<OrderItem> getSameItem(Order order, Long bookId) {
        return order.getOrderItems()
                .stream()
                .filter(item -> item.getBookId().equals(bookId))
                .findFirst();
    }

    private Set<OrderItem> removeItem(Order order, Long bookId) {
        return order.getOrderItems()
                .stream()
                .filter(item -> !item.getBookId().equals(bookId))
                .collect(Collectors.toSet());
    }

    private void setPrice(OrderItem orderItem, Book book) {
        orderItem.setPrice(book.getPrice()
                .multiply(
                        BigDecimal.valueOf(
                                orderItem.getQuantity())
                )
        );
    }

    private Order getOrderByStatusAndUserId(Order.Status status, Long userId) {
        return orderRepository.findByStatusAndUserId(
                        status, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a pending order"));
    }

    private void sendMessage(
            String notificationService,
            String messageType,
            Long chatId,
            Order order) {
        notificationStrategy
                .getNotificationService(
                        notificationService, messageType
                )
                .sendMessage(
                        chatId, order);
    }
}
