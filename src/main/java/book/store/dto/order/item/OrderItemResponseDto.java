package book.store.dto.order.item;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponseDto {
    private Long id;
    private Long orderId;
    private Long bookId;
    private int quantity;
    private Long userId;
    private BigDecimal price;
}
