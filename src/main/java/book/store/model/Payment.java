package book.store.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.net.URL;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = FALSE")
@Accessors(chain = true)
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "rental_id")
    private Long orderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @Column(name = "session_url")
    @EqualsAndHashCode.Exclude
    private URL sessionUrl;

    @NotBlank
    @Column(name = "session_id")
    @EqualsAndHashCode.Exclude
    private String sessionId;

    @NotNull
    @Column(name = "amount_to_pay")
    private BigDecimal amountToPay;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public enum Status {
        PENDING,
        CANCELED,
        EXPIRED,
        PAID;

        public static Status fromString(String value) {
            for (Status status: Status.values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }
}
