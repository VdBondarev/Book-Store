package book.store.controller;

import book.store.dto.user.UserAdminResponseDto;
import book.store.dto.user.UserResponseDto;
import book.store.dto.user.UserSearchParametersDto;
import book.store.dto.user.UserUpdateRequestDto;
import book.store.model.User;
import book.store.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users controller", description = "Endpoints for managing users")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;

    @Operation(summary = "Get your profile's info",
            description = "Endpoint for getting your profile's info")
    @GetMapping("/mine")
    public UserResponseDto getMyInfo(Authentication authentication) {
        User user = getUser(authentication);
        return userService.getMyInfo(user);
    }

    @PutMapping("/mine")
    @Operation(summary = "Update your profile's info",
            description = "Endpoint for updating your profile's info")
    public UserResponseDto updateMyInfo(
            Authentication authentication,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        User user = getUser(authentication);
        return userService.updateMyInfo(user, requestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update a user's role",
            description = "Endpoint for updating the user's role."
                    + " Allowed for managers only")
    public UserAdminResponseDto changeUserRole(
            @PathVariable Long id,
            @RequestParam(name = "role_name") String roleName) {
        return userService.changeUserRole(id, roleName);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Find users",
            description = "Endpoint for finding users by params. Allowed for managers only")
    public List<UserAdminResponseDto> search(
            @RequestBody UserSearchParametersDto parametersDto,
            Pageable pageable) {
        return userService.search(parametersDto, pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Get all users",
            description = "Endpoint for getting all users. Allowed for managers only")
    public List<UserAdminResponseDto> getAll(Pageable pageable) {
        return userService.getAll(pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete a user",
            description = "Endpoint for deleting a user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    private User getUser(Authentication authentication) {
        return (User) authentication.getPrincipal();
    }
}
