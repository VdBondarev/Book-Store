package book.store.dto.book;

import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 *
 * @param priceBetween should contain 1 or 2 elements
 * if one - then it will search books by price from 0 to this param
 * if two - them it will search book by price from the first param to the second
 */
public record BookSearchParametersDto(
        String title,
        String author,
        String description,
        @Max(2)
        List<BigDecimal> priceBetween,
        Set<Long> categoriesIds
) {
}
