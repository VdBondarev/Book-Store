package book.store.repository.specification.user;

import book.store.model.User;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class InLastNamesSpecificationProvider
        implements InSpecificationProvider<User, List<String>> {
    private static final String LAST_NAME_FIELD = "lastName";

    @Override
    public Specification<User> getSpecification(List<String> params) {
        return (root, query, criteriaBuilder) -> root.get(LAST_NAME_FIELD).in(params);
    }

    @Override
    public String getKey() {
        return LAST_NAME_FIELD;
    }
}
