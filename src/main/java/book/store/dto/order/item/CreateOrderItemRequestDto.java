package book.store.dto.order.item;

public record CreateOrderItemRequestDto(
        Long bookId,
        int quantity
) {
}
