package book.store.service.shopping.cart;

import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.dto.shopping.item.CreateCartItemRequestDto;
import book.store.model.User;

public interface ShoppingCartService {
    ShoppingCartResponseDto getMyShoppingCart(User user);

    ShoppingCartResponseDto getUserShoppingCart(Long id);

    ShoppingCartResponseDto addCartItem(User user, CreateCartItemRequestDto requestDto);

    Double getPrice(User user);

    ShoppingCartResponseDto updateAnItem(User user, Long bookId, int quantity);

    ShoppingCartResponseDto deleteBook(User user, Long bookId);
}
