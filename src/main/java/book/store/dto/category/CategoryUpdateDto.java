package book.store.dto.category;

import book.store.annotation.StartsWithCapital;

public record CategoryUpdateDto(
        @StartsWithCapital
        String name,
        @StartsWithCapital
        String description
) {
}
