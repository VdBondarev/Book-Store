package book.store.repository.specification.user.impl;

import book.store.model.User;
import book.store.repository.specification.user.InSpecificationProvider;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class InFirstNamesSpecificationProvider
        implements InSpecificationProvider<User, List<String>> {
    private static final String FIRST_NAME_FIELD = "firstName";

    @Override
    public Specification<User> getSpecification(List<String> params) {
        return (root, query, criteriaBuilder) -> root.get(FIRST_NAME_FIELD).in(params);
    }

    @Override
    public String getKey() {
        return FIRST_NAME_FIELD;
    }
}
