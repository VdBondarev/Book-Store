package book.store.repository.specification.book;

import book.store.dto.book.BookSearchParametersDto;
import book.store.model.Book;
import book.store.repository.specification.SpecificationBuilder;
import book.store.repository.specification.book.impl.CategoriesSpecificationProvider;
import book.store.repository.specification.book.impl.PriceSpecificationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookSpecificationBuilder
        implements SpecificationBuilder<Book, BookSearchParametersDto> {
    private static final String DESCRIPTION = "description";
    private static final String AUTHOR = "author";
    private static final String TITLE = "title";
    private final BookLikeSpecificationProviderManager likeSpecificationProviderManager;
    private final PriceSpecificationProvider priceSpecificationProvider;
    private final CategoriesSpecificationProvider categoriesSpecificationProvider;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParams) {
        Specification<Book> specification = Specification.where(null);
        if (searchParams.categoriesIds() != null && !searchParams.categoriesIds().isEmpty()) {
            specification = specification.and(categoriesSpecificationProvider
                    .getSpecification(
                            searchParams.categoriesIds())
            );
        }
        if (searchParams.priceBetween() != null && !searchParams.priceBetween().isEmpty()) {
            specification = specification.and(
                    priceSpecificationProvider.getSpecification(
                            searchParams.priceBetween())
            );
        }
        specification = getLikeSpecification(
                specification,
                searchParams.description(),
                DESCRIPTION);
        specification = getLikeSpecification(
                specification,
                searchParams.author(),
                AUTHOR);
        specification = getLikeSpecification(
                specification,
                searchParams.title(),
                TITLE);
        return specification;
    }

    private Specification<Book> getLikeSpecification(Specification<Book> specification,
                                                     String param,
                                                     String key) {
        if (notEmpty(param)) {
            specification = specification.and(
                    likeSpecificationProviderManager.getSpecificationProvider(
                                    key
                            )
                            .getSpecification(param)
            );
        }
        return specification;
    }

    private boolean notEmpty(String params) {
        return params != null && !params.isEmpty();
    }
}
