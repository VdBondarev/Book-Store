package book.store.service.user;

import book.store.dto.user.UserAdminResponseDto;
import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.dto.user.UserSearchParametersDto;
import book.store.dto.user.UserUpdateRequestDto;
import book.store.exception.RegistrationException;
import book.store.model.User;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;

    List<UserAdminResponseDto> getAll(Pageable pageable);

    UserAdminResponseDto changeUserRole(Long id, String roleName);

    UserResponseDto getMyInfo(User user);

    UserResponseDto updateMyInfo(User user, UserUpdateRequestDto requestDto);

    List<UserAdminResponseDto> search(UserSearchParametersDto parametersDto, Pageable pageable);

    void delete(Long id);
}
