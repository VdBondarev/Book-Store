package book.store.service.shopping.cart;

import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.dto.shopping.item.CartItemResponseDto;
import book.store.dto.shopping.item.CreateCartItemRequestDto;
import book.store.mapper.CartItemMapper;
import book.store.mapper.ShoppingCartMapper;
import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import book.store.model.User;
import book.store.repository.BookRepository;
import book.store.repository.CartItemRepository;
import book.store.repository.ShoppingCartRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    @Override
    public ShoppingCartResponseDto getMyShoppingCart(User user) {
        return getResponseShoppingCart(user.getId());
    }

    @Override
    public ShoppingCartResponseDto getUserShoppingCart(Long id) {
        return getResponseShoppingCart(id);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto addCartItem(User user, CreateCartItemRequestDto requestDto) {
        if (bookRepository.findByIdWithoutCategories(requestDto.bookId()).isEmpty()) {
            throw new IllegalArgumentException("There is no book by id " + requestDto.bookId());
        }
        ShoppingCart shoppingCart = getShoppingCart(user.getId());
        Optional<CartItem> sameItem = shoppingCart.getCartItems()
                .stream()
                .filter(item -> item.getBook().getId().equals(requestDto.bookId()))
                .findFirst();
        if (sameItem.isPresent()) {
            sameItem.get().setQuantity(sameItem.get().getQuantity() + requestDto.quantity());
            cartItemRepository.save(sameItem.get());
        } else {
            CartItem cartItem = cartItemMapper.toModel(requestDto);
            cartItem.setBook(new Book(requestDto.bookId()));
            shoppingCart.getCartItems().add(cartItem);
            cartItem.setShoppingCart(shoppingCart);
            cartItemRepository.save(cartItem);
        }
        shoppingCartRepository.save(shoppingCart);
        return withMappedCartItems(shoppingCart);
    }

    @Override
    public Double getPrice(User user) {
        return getShoppingCartWithBooks(user.getId())
                .getCartItems()
                .stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(
                                BigDecimal.valueOf(item.getQuantity())
                        )
                )
                .mapToDouble(BigDecimal::doubleValue)
                .sum();
    }

    @Override
    public ShoppingCartResponseDto updateAnItem(User user, Long bookId, int quantity) {
        ShoppingCart shoppingCart = getShoppingCartWithBooks(user.getId());
        CartItem cartItem = findByBookId(shoppingCart, bookId);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        return withMappedCartItems(shoppingCart);
    }

    @Override
    @Transactional
    public ShoppingCartResponseDto deleteBook(User user, Long bookId) {
        ShoppingCart shoppingCart = getShoppingCartWithBooks(user.getId());
        Set<CartItem> cartItems = shoppingCart.getCartItems()
                .stream()
                .filter(cart -> !cart.getBook().getId().equals(bookId))
                .collect(Collectors.toSet());
        if (shoppingCart.getCartItems().size() == cartItems.size()) {
            return withMappedCartItems(shoppingCart);
        }
        CartItem cartItem = findByBookId(shoppingCart, bookId);
        cartItemRepository.delete(cartItem);
        shoppingCart.setCartItems(cartItems);
        shoppingCartRepository.save(shoppingCart);
        return withMappedCartItems(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto clear(User user) {
        ShoppingCart shoppingCart = getShoppingCart(user.getId());
        shoppingCart.getCartItems()
                .forEach(item -> cartItemRepository
                        .deleteById(item.getId())
                );
        shoppingCart.setCartItems(new HashSet<>());
        shoppingCartRepository.save(shoppingCart);
        ShoppingCartResponseDto responseDto = shoppingCartMapper.toResponseDto(shoppingCart);
        responseDto.setCartItems(new HashSet<>());
        return responseDto;
    }

    private CartItem findByBookId(ShoppingCart shoppingCart, Long bookId) {
        return shoppingCart.getCartItems()
                .stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Can't find a book with id "
                                        + bookId
                                        + " in the shopping cart"));
    }

    private ShoppingCartResponseDto getResponseShoppingCart(Long id) {
        ShoppingCart shoppingCart = getShoppingCart(id);
        return withMappedCartItems(shoppingCart);
    }

    private ShoppingCartResponseDto withMappedCartItems(ShoppingCart shoppingCart) {
        Set<CartItemResponseDto> cartItems = shoppingCart.getCartItems()
                .stream()
                .map(cartItemMapper::toResponseDto)
                .collect(Collectors.toSet());
        ShoppingCartResponseDto responseDto =
                shoppingCartMapper.toResponseDto(shoppingCart);
        responseDto.setCartItems(cartItems);
        return responseDto;
    }

    private ShoppingCart getShoppingCart(Long id) {
        return shoppingCartRepository.findByIdWithCartItems(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find the shopping cart for user with id " + id));
    }

    private ShoppingCart getShoppingCartWithBooks(Long id) {
        return shoppingCartRepository.findByIdWithCartItemsAndBooks(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find the shopping cart for user with id " + id));
    }
}
