package book.store.repository.specification.book;

import org.springframework.data.jpa.domain.Specification;

public interface LikeSpecificationProvider<T, P> {
    Specification<T> getSpecification(P params);

    String getKey();
}
