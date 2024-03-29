package book.store.service;

import book.store.dto.UserAdminResponseDto;
import book.store.dto.UserRegistrationRequestDto;
import book.store.dto.UserResponseDto;
import book.store.dto.UserUpdateRequestDto;
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
}
