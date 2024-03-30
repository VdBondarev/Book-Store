package book.store.repository.specification.book.impl;

import book.store.model.Book;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class PriceSpecificationProvider {
    private static final String PRICE_FIELD = "price";

    public Specification<Book> getSpecification(List<BigDecimal> params) {
        BigDecimal priceFrom;
        BigDecimal priceTo;
        if (params.size() == 1) {
            priceFrom = BigDecimal.ZERO;
            priceTo = params.get(0);
        } else {
            priceFrom = params.get(0);
            priceTo = params.get(1);
        }
        if (priceFrom.compareTo(priceTo) >= 0) {
            throw new IllegalArgumentException("Price from should be bigger than price to, "
                    + "but was " + params);
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get(PRICE_FIELD), priceFrom, priceTo);
    }

    public String getKey() {
        return PRICE_FIELD;
    }
}
