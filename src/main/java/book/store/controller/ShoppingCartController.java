package book.store.controller;

import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.dto.shopping.item.CreateCartItemRequestDto;
import book.store.model.User;
import book.store.service.shopping.cart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @PostMapping
    @Operation(summary = "Add a cart item to your shopping cart",
            description = """
                    Point book_id and quantity param.
                    Depending on these params, a new cart item will be created.
                    This cart item will be linked to your shopping cart and saved.
                    If you try to add a book that already is added,
                    then you will just update quantity (old quantity + new quantity).
                    To update it back, go to update endpoint
                    """)
    public ShoppingCartResponseDto addCartItem(
            Authentication authentication,
            @RequestBody @Valid CreateCartItemRequestDto requestDto) {
        return shoppingCartService.addCartItem(getUser(authentication), requestDto);
    }

    @GetMapping
    @Operation(summary = "See you shopping cart with all items")
    public ShoppingCartResponseDto getMyShoppingCart(Authentication authentication) {
        return shoppingCartService.getMyShoppingCart(getUser(authentication));
    }

    @GetMapping("/price")
    @Operation(summary = "Get the total price of all your items added to the shopping cart")
    public Double getPrice(Authentication authentication) {
        return shoppingCartService.getPrice(getUser(authentication));
    }

    @PutMapping
    @Operation(summary = "Update quantity of a book in your shopping cart")
    public ShoppingCartResponseDto updateBookQuantity(
            Authentication authentication,
            @RequestParam(name = "book_id") Long bookId,
            @RequestParam @Min(1) int quantity) {
        return shoppingCartService.updateAnItem(getUser(authentication), bookId, quantity);
    }

    @PutMapping("/clear")
    @Operation(summary = "Clear the whole shopping cart")
    public ShoppingCartResponseDto clear(Authentication authentication) {
        return shoppingCartService.clear(getUser(authentication));
    }

    @DeleteMapping
    @Operation(summary = "Delete a book from your shopping cart")
    public ShoppingCartResponseDto deleteBook(
            Authentication authentication,
            @RequestParam(name = "book_id") Long bookId) {
        return shoppingCartService.deleteBook(getUser(authentication), bookId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "See user's shopping cart",
            description = """
                    Get user's shopping cart by user id.
                    Allowed for admins only
                    """)
    public ShoppingCartResponseDto getUserShoppingCart(@PathVariable Long id) {
        return shoppingCartService.getUserShoppingCart(id);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }

}
