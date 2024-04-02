package book.store.controller;

import book.store.dto.order.OrderResponseDto;
import book.store.dto.order.OrderWithoutOrderItemsResponseDto;
import book.store.model.User;
import book.store.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    @Operation(summary = "Place an order",
            description = """           
                    If you already have a PENDING (not PAID) order
                    You will not be able to place an order.
                             
                    Retrieving all cart items from a shopping cart.
                    
                    But if your shopping cart is empty, you will not be able to place an order.
                    
                    Then converting cart items into order items.
                    
                    Marking a created order (with these order items) as PENDING.
                    
                    Then you should pay for the order and it will be marked as PAID.
                    
                    Otherwise the next day your order will be canceled automatically.
                    
                    You can cancel an order as well (send request to cancel endpoint).
                    
                    After creating an order, your shopping cart will be empty
                    """)
    public OrderResponseDto placeOrder(
            Authentication authentication,
            @RequestParam(name = "shipping_address") String shippingAddress) {
        return orderService.placeOrder(getUser(authentication), shippingAddress);
    }

    @PutMapping("/cancel")
    @Operation(summary = "Cancel your PENDING order",
            description = """
                    Cancels only a pending order (if it exists).
                    
                    Otherwise an exception will be thrown.
                    
                    After canceling you can create a new order again
                    """)
    public void cancel(Authentication authentication) {
        orderService.cancel(getUser(authentication));
    }

    @GetMapping
    @Operation(summary = "Get your orders history",
            description = """
                    Shows your order history.
                    
                    But without order items.
                    
                    If you want to get order items as well
                    you can go to getOrder endpoint and pass an order id to link
                    """)
    public List<OrderWithoutOrderItemsResponseDto> getHistory(
            Authentication authentication,
            Pageable pageable) {
        return orderService.getHistory(getUser(authentication), pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific order",
            description = """
                    Shows a pointed order, only if it is yours
                    """)
    public OrderResponseDto getOrder(
            Authentication authentication,
            @PathVariable Long id) {
        return orderService.getOrder(getUser(authentication), id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update status of a pointed order")
    public void updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        orderService.updateStatus(id, status);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
