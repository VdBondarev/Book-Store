package book.store.repository.specification.user.impl;

import book.store.model.User;
import java.util.Set;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RolesIdsSpecificationProvider {
    private static final String ROLES_FIELD = "roles";
    private static final String ID = "id";

    public Specification<User> getSpecification(Set<Long> params) {
        return (root, query, criteriaBuilder) -> root.get(ROLES_FIELD).get(ID).in(params);
    }
}
