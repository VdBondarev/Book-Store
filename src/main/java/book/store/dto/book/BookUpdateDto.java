package book.store.dto.book;

import book.store.annotation.StartsWithCapital;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Set;
import org.hibernate.validator.constraints.ISBN;

public record BookUpdateDto(
        @StartsWithCapital
        String title,
        @StartsWithCapital
        String author,
        @StartsWithCapital
        String description,
        @StartsWithCapital
        String coverImage,
        @ISBN
        String isbn,
        @Min(0)
        BigDecimal price,
        Set<Long> categoriesIds
) {
}
