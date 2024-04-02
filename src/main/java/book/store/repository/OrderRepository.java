package book.store.repository;

import book.store.model.Order;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserIdAndStatus(Long userId, Order.Status status);

    List<Order> findByUserId(Long userId, Pageable pageable);

    @Query("FROM Order order "
            + "LEFT JOIN FETCH order.orderItems "
            + "WHERE order.userId = :userId AND order.id = :id")
    Optional<Order> findByUserIdWithOrderItems(Long userId, Long id);

    @Query("FROM Order order "
            + "LEFT JOIN FETCH order.orderItems "
            + "WHERE order.id = :id")
    Optional<Order> findByIdWithOrderItems(Long id);

    @Query("FROM Order order "
            + "WHERE order.status = :status AND order.orderDate < :now")
    List<Order> findAllByStatusAndOrderDate(Order.Status status, LocalDate now);
}
