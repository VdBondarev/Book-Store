package book.store.dto;

import book.store.annotation.StartsWithCapital;
import jakarta.validation.constraints.Email;
import org.hibernate.validator.constraints.Length;

public record UserUpdateRequestDto(
        @StartsWithCapital
        String firstName,
        @StartsWithCapital
        String lastName,
        @Email
        String email,
        @Length(min = 8, max = 35)
        String password
) {
}
