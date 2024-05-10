package book.store.controller;

import book.store.dto.payment.PaymentResponseDto;
import book.store.model.User;
import book.store.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments controller", description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a payment depending on a pending order")
    public PaymentResponseDto create(Authentication authentication)
            throws StripeException, MalformedURLException {
        return paymentService.create(getUser(authentication));
    }

    @GetMapping("/pending")
    @Operation(summary = "Get a pending payment")
    public PaymentResponseDto getPending(Authentication authentication) {
        return paymentService.getPending(getUser(authentication));
    }

    @GetMapping("/cancel")
    @Operation(summary = "Cancel a pending payment")
    public PaymentResponseDto cancel(Authentication authentication) {
        return paymentService.cancel(getUser(authentication));
    }

    @GetMapping("/success")
    @Operation(summary = "Successful payment")
    public PaymentResponseDto success(Authentication authentication) {
        return paymentService.success(getUser(authentication));
    }

    @Operation(summary = "Get user's payments",
            description = "Endpoint for getting pointed user's payments."
                    + " Allowed for admin only")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public List<PaymentResponseDto> getUserPayments(
            @RequestParam(name = "user_id") Long userId,
            Pageable pageable) {
        return paymentService.getUserPayments(userId, pageable);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
