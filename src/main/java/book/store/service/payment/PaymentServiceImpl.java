package book.store.service.payment;

import book.store.dto.payment.PaymentResponseDto;
import book.store.mapper.PaymentMapper;
import book.store.model.Order;
import book.store.model.Payment;
import book.store.model.User;
import book.store.repository.OrderRepository;
import book.store.repository.PaymentRepository;
import book.store.util.StripeUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentMapper paymentMapper;
    private final StripeUtil stripeUtil;
    @Value("${stripe.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public PaymentResponseDto create(User user)
            throws StripeException, MalformedURLException {
        if (paymentRepository.findByUserIdAndStatus(
                user.getId(), Payment.Status.PENDING)
                .isPresent()) {
            throw new IllegalArgumentException("""
                    Can't create a new payment. User already has one pending.
                    They should pay for that first or cancel it.
                    """);
        }
        Order order = orderRepository.findByUserIdAndStatus(
                user.getId(), Order.Status.PENDING)
                .orElseThrow(() -> new EntityNotFoundException("""
                        User have not created an order yet.
                        User should have a pending order to create a payment.
                        """));
        Session session = stripeUtil.createSession(
                order.getPrice().longValue(),
                order.getShippingAddress() + " order");

        Payment payment = new Payment()
                .setStatus(Payment.Status.PENDING)
                .setOrderId(order.getId())
                .setAmountToPay(order.getPrice())
                .setSessionId(session.getId())
                .setUserId(order.getUserId())
                .setSessionUrl(new URL(session.getUrl()));
        paymentRepository.save(payment);
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    public PaymentResponseDto getPending(User user) {
        Payment payment = getPayment(user.getId(), Payment.Status.PENDING);
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    public PaymentResponseDto cancel(User user) {
        Payment payment = getPayment(user.getId(), Payment.Status.PENDING);
        payment.setDeleted(true);
        payment.setStatus(Payment.Status.CANCELED);
        paymentRepository.save(payment);
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto success(User user) {
        Payment payment = getPayment(user.getId(), Payment.Status.PENDING);
        Order order = orderRepository.findByUserIdAndStatus(
                user.getId(), Order.Status.PENDING)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find and order for user with id " + user.getId()));
        payment.setStatus(Payment.Status.PAID);
        order.setStatus(Order.Status.PAID);
        paymentRepository.save(payment);
        orderRepository.save(order);
        return paymentMapper.toResponseDto(payment);
    }

    private Payment getPayment(Long userId, Payment.Status status) {
        return paymentRepository.findByUserIdAndStatus(userId, status)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a "
                                + status.name()
                                + " payment for user with id " + userId));
    }
}
