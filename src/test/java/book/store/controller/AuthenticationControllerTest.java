package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_USERS_FILE_PATH;
import static book.store.holder.LinksHolder.DELETE_ALL_USER_ROLES_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_USER_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.user.UserLoginRequestDto;
import book.store.dto.user.UserLoginResponseDto;
import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {
    protected static MockMvc mockMvc;
    private static final String JWT_PATTERN =
            "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_.+/=]*$";
    @Autowired
    private ObjectMapper objectMapper;

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
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that registration endpoint works fine with valid input params")
    void register_ValidInput_Success() throws Exception {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                "test@email.com",
                "Test",
                "Test",
                "12345678",
                "12345678"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/authentication/registration")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        UserResponseDto expected = createUserResponseDto(1L, requestDto);

        UserResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify that registration endpoint works as expected
            When passing already registered email
            """)
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    INSERT_USER_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    void register_AlreadyRegisteredEmail_Failure() throws Exception {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                "user@example.com",
                "Test",
                "Test",
                "12345678",
                "12345678"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(
                        post("/authentication/registration")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        Exception exception = result.getResolvedException();

        String expected = """
                Can't register a new user
                Passed email already exists
                Try another one
                """;
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify that registration endpoint works as expected
            When passing non-valid params
            """)
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    INSERT_USER_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    void register_NonValidParam_Failure() throws Exception {
        UserRegistrationRequestDto requestDto = createUserRegistrationRequestDto(
                "non-valid",
                "non-valid",
                "non-valid",
                "1234",
                "1234"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        // expecting that input validation will work and will not let a request go further
        mockMvc.perform(
                        post("/authentication/registration")
                                .content(content)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    INSERT_USER_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_USERS_FILE_PATH,
                    DELETE_ALL_USER_ROLES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Verify that login() method works as expected")
    void login_ValidInput_Success() throws Exception {
        UserLoginRequestDto requestDto =
                new UserLoginRequestDto("user@example.com", "1234567890");

        String content = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(get("/authentication/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto responseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class
        );

        assertTrue(responseDto.token().matches(JWT_PATTERN));
    }

    private UserRegistrationRequestDto createUserRegistrationRequestDto(
            String email,
            String firstName,
            String lastName,
            String password,
            String repeatPassword
    ) {
        return new UserRegistrationRequestDto(
                email,
                firstName,
                lastName,
                password,
                repeatPassword
        );
    }

    private UserResponseDto createUserResponseDto(Long id, UserRegistrationRequestDto requestDto) {
        return new UserResponseDto(
                id,
                requestDto.firstName(),
                requestDto.lastName(),
                requestDto.email()
        );
    }
}
