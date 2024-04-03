package book.store.controller;

import book.store.dto.payment.PaymentResponseDto;
import book.store.model.User;
import book.store.service.payment.PaymentService;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.MalformedURLException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Payments controller", description = "Endpoints for managing payments")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentsController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponseDto create(Authentication authentication)
            throws StripeException, MalformedURLException {
        return paymentService.create(getUser(authentication));
    }

    @GetMapping("/pending")
    public PaymentResponseDto getPending(Authentication authentication) {
        return paymentService.getPending(getUser(authentication));
    }

    @GetMapping("/cancel")
    public PaymentResponseDto cancel(Authentication authentication) {
        return paymentService.cancel(getUser(authentication));
    }

    @GetMapping("/success")
    public PaymentResponseDto success(Authentication authentication) {
        return paymentService.success(getUser(authentication));
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
