package book.store.repository.specification.book.impl;

import book.store.model.Book;
import book.store.repository.specification.book.LikeSpecificationProvider;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TitleSpecificationProvider
        implements LikeSpecificationProvider<Book, String> {
    private static final String TITLE_FIELD = "title";
    private static final String PERCENT = "%";

    @Override
    public Specification<Book> getSpecification(String params) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        root.get(TITLE_FIELD), PERCENT + params + PERCENT
                );
    }

    @Override
    public String getKey() {
        return TITLE_FIELD;
    }
}
