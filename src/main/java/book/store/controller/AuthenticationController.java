package book.store.controller;

import book.store.dto.user.UserLoginRequestDto;
import book.store.dto.user.UserLoginResponseDto;
import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.security.AuthenticationService;
import book.store.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication controller", description = "Endpoints for authentication")
@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    @Operation(summary = "Registration for any user")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        return userService.register(requestDto);
    }

    @GetMapping("/login")
    @Operation(summary = "Login for signed-up users")
    public UserLoginResponseDto login(
            @RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }
}
