package book.store.repository;

import book.store.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    @Query("FROM ShoppingCart cart "
            + "LEFT JOIN FETCH cart.cartItems "
            + "WHERE cart.userId = :userId")
    Optional<ShoppingCart> findByIdWithCartItems(Long userId);

    @Query("FROM ShoppingCart cart "
            + "LEFT JOIN FETCH cart.cartItems item "
            + "LEFT JOIN FETCH item.book "
            + "WHERE cart.userId = :userId")
    Optional<ShoppingCart> findByIdWithCartItemsAndBooks(Long userId);
}
