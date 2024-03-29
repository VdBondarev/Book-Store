package book.store.dto;

import java.util.List;
import java.util.Set;

public record UserSearchParametersDto(
        List<String> emails,
        List<String> firstNames,
        List<String> lastNames,
        Set<Long> rolesIds
) {
}
