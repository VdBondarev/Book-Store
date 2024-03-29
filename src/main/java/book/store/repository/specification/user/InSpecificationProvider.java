package book.store.repository.specification.user;

import org.springframework.data.jpa.domain.Specification;

public interface InSpecificationProvider<T, P> {
    Specification<T> getSpecification(P params);

    String getKey();
}
