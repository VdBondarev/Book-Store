package book.store.dto;

import java.util.Set;
import lombok.Data;

@Data
public class UserAdminResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Long> rolesIds;
}
