package book.store.dto.book;

import book.store.annotation.StartsWithCapital;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.Set;
import org.hibernate.validator.constraints.ISBN;

public record BookCreateRequestDto(
        @NotEmpty
        @StartsWithCapital
        String title,
        @NotEmpty
        @StartsWithCapital
        String author,
        @ISBN
        String isbn,
        @Min(0)
        BigDecimal price,
        @StartsWithCapital
        String description,
        String coverImage,
        @NotEmpty
        Set<Long> categoriesIds
) {
}
