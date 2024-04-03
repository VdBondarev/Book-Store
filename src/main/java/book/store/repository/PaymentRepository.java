package book.store.repository;

import book.store.model.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUserIdAndStatus(Long userId, Payment.Status status);

    List<Payment> findAllByUserId(Long userId, Pageable pageable);
}
