package book.store.service.order;

import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.OrderWithoutOrderItemsResponseDto;
import book.store.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponseDto placeOrder(User user, String shippingAddress);

    void cancel(User user);

    List<OrderWithoutOrderItemsResponseDto> getHistory(User user, Pageable pageable);

    OrderResponseDto getOrder(User user, Long id);

    void updateStatus(Long id, String status);
}
