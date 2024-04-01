package book.store.controller;

import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.dto.shopping.item.CreateCartItemRequestDto;
import book.store.model.User;
import book.store.service.shopping.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping carts controller", description = "Endpoints for managing shopping carts")
@RestController
@RequiredArgsConstructor
@RequestMapping("/shopping/carts")
public class ShoppingCartsController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    public ShoppingCartResponseDto addCartItem(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        return shoppingCartService.addCartItem(getUser(authentication), requestDto);
    }

    @GetMapping
    public ShoppingCartResponseDto getMyShoppingCart(Authentication authentication) {
        return shoppingCartService.getMyShoppingCart(getUser(authentication));
    }

    @GetMapping("/price")
    public Double getPrice(Authentication authentication) {
        return shoppingCartService.getPrice(getUser(authentication));
    }

    @PutMapping
    public ShoppingCartResponseDto updateBookQuantity(
            Authentication authentication,
            @RequestParam(name = "book_id") Long bookId,
            @RequestParam int quantity) {
        return shoppingCartService.updateAnItem(getUser(authentication), bookId, quantity);
    }

    @DeleteMapping
    public ShoppingCartResponseDto deleteBook(
            Authentication authentication,
            @RequestParam(name = "book_id") Long bookId) {
        return shoppingCartService.deleteBook(getUser(authentication), bookId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ShoppingCartResponseDto getUserShoppingCart(@PathVariable Long id) {
        return shoppingCartService.getUserShoppingCart(id);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

}
