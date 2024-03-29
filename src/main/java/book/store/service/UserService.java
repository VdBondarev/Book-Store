package book.store.service;

import book.store.dto.UserRegistrationRequestDto;
import book.store.dto.UserResponseDto;
import book.store.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException;
}
