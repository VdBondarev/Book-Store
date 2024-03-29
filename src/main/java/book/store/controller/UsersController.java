package book.store.controller;

import book.store.dto.UserAdminResponseDto;
import book.store.dto.UserResponseDto;
import book.store.dto.UserUpdateRequestDto;
import book.store.model.User;
import book.store.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @GetMapping("/mine")
    public UserResponseDto getMyInfo(Authentication authentication) {
        User user = getUser(authentication);
        return userService.getMyInfo(user);
    }

    @PutMapping("/mine")
    public UserResponseDto updateMyInfo(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        User user = getUser(authentication);
        return userService.updateMyInfo(user, requestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserAdminResponseDto changeUserRole(
            @PathVariable Long id,
            @RequestParam String roleName) {
        return userService.changeUserRole(id, roleName);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserAdminResponseDto> getAll(Pageable pageable) {
        return userService.getAll(pageable);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
