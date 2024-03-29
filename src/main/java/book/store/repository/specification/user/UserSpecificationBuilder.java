package book.store.repository.specification.user;

import book.store.dto.UserSearchParametersDto;
import book.store.model.User;
import book.store.repository.specification.SpecificationBuilder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSpecificationBuilder
        implements SpecificationBuilder<User, UserSearchParametersDto> {
    private static final String FIRST_NAME_FIELD = "firstName";
    private static final String LAST_NAME_FIELD = "lastName";
    private static final String EMAIL_FIELD = "email";
    private final UserInSpecificationProviderManager specificationProviderManager;
    private final RolesIdsSpecificationProvider rolesIdsSpecificationProvider;

    @Override
    public Specification<User> build(UserSearchParametersDto searchParams) {
        Specification<User> specification = Specification.where(null);
        if (notEmpty(searchParams.emails())) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(EMAIL_FIELD)
                    .getSpecification(searchParams.emails()));
        }
        if (notEmpty(searchParams.firstNames())) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(FIRST_NAME_FIELD)
                    .getSpecification(searchParams.firstNames()));
        }
        if (notEmpty(searchParams.lastNames())) {
            specification = specification.and(specificationProviderManager
                    .getSpecificationProvider(LAST_NAME_FIELD)
                    .getSpecification(searchParams.lastNames()));
        }
        if (searchParams.rolesIds() != null && !searchParams.rolesIds().isEmpty()) {
            specification = specification.and(rolesIdsSpecificationProvider
                    .getSpecification(searchParams.rolesIds()));
        }
        return specification;
    }

    private boolean notEmpty(List<String> params) {
        return params != null && !params.isEmpty();
    }
}
