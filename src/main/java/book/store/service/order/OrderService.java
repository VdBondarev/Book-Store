package book.store.service.order;

import book.store.dto.order.OrderResponseDto;
import book.store.model.User;

public interface OrderService {
    OrderResponseDto placeOrder(User user, String shippingAddress);
}
