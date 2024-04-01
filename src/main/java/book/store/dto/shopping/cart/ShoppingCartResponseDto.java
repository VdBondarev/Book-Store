package book.store.dto.shopping.cart;

import book.store.dto.shopping.item.CartItemResponseDto;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShoppingCartResponseDto {
    private Long userId;
    private Set<CartItemResponseDto> cartItems;
}
