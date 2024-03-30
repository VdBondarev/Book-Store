package book.store.repository.specification.book.impl;

import book.store.model.Book;
import book.store.repository.specification.book.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class DescriptionSpecificationProvider
        implements LikeSpecificationProvider<Book, String> {
    private static final String DESCRIPTION_FIELD = "description";
    private static final String PERCENT = "%";

    @Override
    public Specification<Book> getSpecification(String params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        root.get(DESCRIPTION_FIELD), PERCENT + params + PERCENT
                );
    }

    @Override
    public String getKey() {
        return DESCRIPTION_FIELD;
    }
}
