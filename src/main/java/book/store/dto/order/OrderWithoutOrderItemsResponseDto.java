package book.store.dto.order;

import book.store.model.Order;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderWithoutOrderItemsResponseDto {
    private Long id;
    private Long userId;
    private LocalDate orderDate;
    private BigDecimal price;
    private String shippingAddress;
    private Order.Status status;
}
