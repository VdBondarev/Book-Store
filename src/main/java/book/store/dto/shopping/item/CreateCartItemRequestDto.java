package book.store.dto.shopping.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequestDto(
        @NotNull
        Long bookId,
        @NotNull
        @Min(0)
        int quantity
) {
}
