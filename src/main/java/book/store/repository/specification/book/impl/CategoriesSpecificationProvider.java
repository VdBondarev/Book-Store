package book.store.repository.specification.book.impl;

import book.store.model.Book;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CategoriesSpecificationProvider {
    private static final String CATEGORIES_FIELD = "categories";
    private static final String ID = "id";

    public Specification<Book> getSpecification(Set<Long> params) {
        return (root, query, criteriaBuilder) -> root.get(CATEGORIES_FIELD).get(ID).in(params);
    }

    public String getKey() {
        return CATEGORIES_FIELD;
    }
}
