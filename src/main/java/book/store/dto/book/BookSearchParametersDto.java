package book.store.dto.book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 *
 * @param priceBetween should contain 1 or 2 elements
 * if one - then it will search books by price from 0 to this param
 * if two - then it will search books by price from the first param to the second
 */
public record BookSearchParametersDto(
        String title,
        String author,
        String description,
        List<BigDecimal> priceBetween,
        Set<Long> categoriesIds
) {
}
