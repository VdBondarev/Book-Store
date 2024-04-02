package book.store.controller;

import book.store.dto.order.OrderResponseDto;
import book.store.model.User;
import book.store.service.order.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Orders controller", description = "Endpoint for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto placeOrder(
            Authentication authentication,
            @RequestParam(name = "shipping_address") String shippingAddress) {
        return orderService.placeOrder(getUser(authentication), shippingAddress);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
