package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_USERS_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_USER_FILE_PATH;
import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.user.UserAdminResponseDto;
import book.store.dto.user.UserResponseDto;
import book.store.dto.user.UserUpdateRequestDto;
import book.store.model.User;
import book.store.security.JwtUtil;
import book.store.telegram.strategy.notification.AdminNotificationStrategy;
import book.store.telegram.strategy.notification.user.UserDeletingNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {
    protected static MockMvc mockMvc;
    private static final String PASSWORD = "1234567890";
    private static final String BEARER = "Bearer";
    private static final String EMPTY = " ";
    private static final String EMAIL = "user@example.com";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;
    private String jwt;
    @MockBean
    private AdminNotificationStrategy<User> notificationStrategy;

    @BeforeEach
    void setUp() {
        jwt = jwtUtil.generateToken(EMAIL);
    }

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        EMAIL, PASSWORD
                )
        );

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
            Verify that updateMyInfo() endpoint works as expected with valid params
            """)
    @Test
    public void updateMyInfo_ValidInput_Success() throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        EMAIL, PASSWORD
                )
        );

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(
                "New",
                "New",
                "new@example.com",
                "11111111111111"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/mine")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header(AUTHORIZATION, BEARER + EMPTY + jwt)
                )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        UserResponseDto expected = new UserResponseDto(
                1L,
                "New",
                "New",
                "new@example.com"
        );

        assertEquals(expected, actual);
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
            Verify that updateMyInfo() endpoint works as expected with already registered email
            """)
    @Test
    public void updateMyInfo_AlreadyRegisteredEmail_Failure() throws Exception {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        EMAIL, PASSWORD
                )
        );

        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(
                "New",
                "New",
                // expecting that this email is already registered
                EMAIL,
                "11111111111111"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(put("/users/mine")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header(AUTHORIZATION, BEARER + EMPTY + jwt)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String message = result.getResolvedException().getMessage();

        String expectedMessage = """
                Can't update email
                This one is already taken
                Try another one
                """;

        assertEquals(expectedMessage, message);
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
            Verify that validation is completed properly with non-valid request dto
            Email is not well-formed
            First name and last name do not start with upper case
            Password is of length less than 8 characters
            """)
    @Test
    public void updateMyInfo_NonValidRequest_Failure() throws Exception {
        UserUpdateRequestDto requestDto = new UserUpdateRequestDto(
                "non-valid",
                "non-valid",
                "non-valid",
                "short"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        EMAIL, PASSWORD
                )
        );

        mockMvc.perform(put("/users/mine")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .principal(authentication)
                        .header(AUTHORIZATION, BEARER + EMPTY + jwt)
        )
                .andExpect(status().isBadRequest());
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
            Verify that delete() endpoint works as expected
            And getAll() as well (after deleting an only user, getAll() should return an empty list)
            """)
    @Test
    @WithMockUser(username = EMAIL, password = PASSWORD, authorities = "ROLE_ADMIN")
    public void delete_ValidRequest_Success() throws Exception {
        Long id = 1L;

        when(notificationStrategy.getNotificationService(anyString(), anyString()))
                .thenReturn(mock(UserDeletingNotificationService.class));

        // deleting a user
        mockMvc.perform(
                delete("/users/" + id)
        )
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(
                        get("/users")
                )
                .andExpect(status().isOk())
                .andReturn();

        UserAdminResponseDto[] users = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserAdminResponseDto[].class
        );

        // expecting that there will be no users in db
        assertEquals(0, users.length);
    }
}

