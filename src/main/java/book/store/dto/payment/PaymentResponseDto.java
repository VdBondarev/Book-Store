package book.store.dto.payment;

import book.store.model.Payment;
import java.math.BigDecimal;
import java.net.URL;

public record PaymentResponseDto(
        Long id,
        Long userId,
        Long orderId,
        Payment.Status status,
        URL sessionUrl,
        String sessionId,
        BigDecimal amountToPay
) {
}
