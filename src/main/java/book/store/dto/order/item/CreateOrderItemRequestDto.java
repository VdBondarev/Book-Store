package book.store.dto.order.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateOrderItemRequestDto(
        @Min(1)
        @NotNull
        Long bookId,
        @Min(1)
        @NotNull
        int quantity
) {
}
