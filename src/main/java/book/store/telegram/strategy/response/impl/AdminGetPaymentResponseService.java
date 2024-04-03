package book.store.telegram.strategy.response.impl;

import book.store.model.Payment;
import book.store.repository.PaymentRepository;
import book.store.telegram.strategy.response.AdminResponseService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminGetPaymentResponseService implements AdminResponseService {
    private static final String PAYMENT_REGEX =
            "^(?i)Get info about a payment with id:\\s*\\d+$";
    private final PaymentRepository paymentRepository;

    @Override
    public String getMessage(String text) {
        Long id = getId(text);
        Optional<Payment> paymentOptional = paymentRepository.findById(id);
        if (paymentOptional.isEmpty()) {
            return "There is no payment by id " + id;
        }
        Payment payment = paymentOptional.get();
        String message = """
                ***
                Found this payment.
                
                Id: %s,
                User id: %s,
                Order id: %s,
                Status: %s,
                Amount to pay: %s.
                ***
                """;
        return String.format(
                message,
                payment.getId(),
                payment.getUserId(),
                payment.getOrderId(),
                payment.getStatus().name(),
                payment.getAmountToPay()
        );
    }

    @Override
    public boolean isApplicable(String text) {
        return text.matches(PAYMENT_REGEX);
    }
}
