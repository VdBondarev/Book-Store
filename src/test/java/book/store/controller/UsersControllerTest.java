package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_USERS_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_USER_FILE_PATH;
import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.user.UserResponseDto;
import book.store.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {
    private static final String BEARER = "Bearer";
    private static final String EMPTY = " ";
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    INSERT_USER_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that getMyInfo() endpoint works as expected
            """)
    @Test
    public void getMyInfo_ValidInput_Success() throws Exception {
        String email = "user@example.com";

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email, "1234567890"
                )
        );

        String jwt = jwtUtil.generateToken(email);

        MvcResult result = mockMvc.perform(get("/users/mine")
                        .principal(authentication)
                        .header(AUTHORIZATION, BEARER + EMPTY + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        UserResponseDto expected = new UserResponseDto(1L, "User", "User", "user@example.com");

        assertEquals(expected, actual);
    }
}
/**
 * @Tag(name = "Users controller", description = "Endpoints for managing users")
 * @RestController
 * @RequestMapping("/users")
 * @RequiredArgsConstructor
 * public class UsersController {
 *     private final UserService userService;
 *
 *     @Operation(summary = "Get your profile's info")
 *     @GetMapping("/mine")
 *     public UserResponseDto getMyInfo(Authentication authentication) {
 *         User user = getUser(authentication);
 *         return userService.getMyInfo(user);
 *     }
 *
 *     @PutMapping("/mine")
 *     @Operation(summary = "Update your profile's info")
 *     public UserResponseDto updateMyInfo(
 *             Authentication authentication,
 *             @RequestBody @Valid UserUpdateRequestDto requestDto) {
 *         User user = getUser(authentication);
 *         return userService.updateMyInfo(user, requestDto);
 *     }
 *
 *     @PutMapping("/{id}")
 *     @PreAuthorize("hasRole('ROLE_ADMIN')")
 *     @Operation(summary = "Update a user's role",
 *             description = """
 *                     Endpoint for updating the user's role.
 *                     Allowed for admins only
 *                     """)
 *     public UserAdminResponseDto changeUserRole(
 *             @PathVariable Long id,
 *             @RequestParam(name = "role_name") String roleName) {
 *         return userService.changeUserRole(id, roleName);
 *     }
 *
 *     @GetMapping("/search")
 *     @PreAuthorize("hasRole('ROLE_ADMIN')")
 *     @Operation(summary = "Find users",
 *             description = "Endpoint for finding users by params. Allowed for admins only")
 *     public List<UserAdminResponseDto> search(
 *             @RequestBody UserSearchParametersDto parametersDto,
 *             Pageable pageable) {
 *         return userService.search(parametersDto, pageable);
 *     }
 *
 *     @GetMapping
 *     @PreAuthorize("hasRole('ROLE_ADMIN')")
 *     @Operation(summary = "Get all users",
 *             description = "Endpoint for getting all users. Allowed for admins only")
 *     public List<UserAdminResponseDto> getAll(Pageable pageable) {
 *         return userService.getAll(pageable);
 *     }
 *
 *     @DeleteMapping("/{id}")
 *     @PreAuthorize("hasRole('ROLE_ADMIN')")
 *     @Operation(summary = "Delete a user",
 *             description = "Endpoint for deleting a user. Allowed for admins only")
 *     @ResponseStatus(HttpStatus.NO_CONTENT)
 *     public void delete(@PathVariable Long id) {
 *         userService.delete(id);
 *     }
 *
 *     private User getUser(Authentication authentication) {
 *         return (User) authentication.getPrincipal();
 *     }
 * }
 */