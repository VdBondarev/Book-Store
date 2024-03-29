package book.store.service;

import book.store.dto.UserAdminResponseDto;
import book.store.dto.UserRegistrationRequestDto;
import book.store.dto.UserResponseDto;
import book.store.dto.UserUpdateRequestDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.Role;
import book.store.model.User;
import book.store.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int ONE = 1;
    private static final int TWO = 2;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new RegistrationException("""
                    Can't register a new user.
                    Passed email already exists.
                    Try another one.
                    """);
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    @Override
    public List<UserAdminResponseDto> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .stream()
                .map(userMapper::toAdminResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserAdminResponseDto changeUserRole(Long id, String roleName) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(
                        "Can't find a user by id " + id));
        Role.RoleName role = Role.RoleName.fromString(roleName);
        if (userIs(user, Role.RoleName.ROLE_USER) && role.equals(Role.RoleName.ROLE_USER)) {
            return userMapper.toAdminResponseDto(user);
        }
        if (userIs(user, Role.RoleName.ROLE_ADMIN) && role.equals(Role.RoleName.ROLE_ADMIN)) {
            return userMapper.toAdminResponseDto(user);
        }
        if (userIs(user, Role.RoleName.ROLE_USER) && role.equals(Role.RoleName.ROLE_ADMIN)) {
            user.getRoles().add(new Role(2L));
        } else {
            user.setRoles(Set.of(new Role(1L)));
        }
        userRepository.save(user);
        return userMapper.toAdminResponseDto(user);
    }

    @Override
    public UserResponseDto getMyInfo(User user) {
        return userMapper.toResponseDto(user);
    }

    @Override
    public UserResponseDto updateMyInfo(User user, UserUpdateRequestDto requestDto) {
        if (requestDto.email() != null
                && userRepository.findByEmail(requestDto.email()).isPresent()) {
            throw new IllegalArgumentException("""
                    Can't update email. This one is already taken.
                    Try another one.
                    """);
        }
        userMapper.toModel(user, requestDto);
        userRepository.save(user);
        return userMapper.toResponseDto(user);
    }

    private boolean userIs(User user, Role.RoleName roleName) {
        if (roleName.equals(Role.RoleName.ROLE_USER)) {
            return user.getRoles().size() == ONE;
        }
        return user.getRoles().size() == TWO;
    }
}
