package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_BOOKS_FILE_PATH;
import static book.store.holder.LinksHolder.DELETE_ALL_CATEGORIES_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_BOOKS_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_CATEGORY_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.BookUpdateDto;
import book.store.model.Book;
import book.store.model.Role;
import book.store.model.User;
import book.store.telegram.strategy.notification.AdminNotificationStrategy;
import book.store.telegram.strategy.notification.book.BookCreationNotificationService;
import book.store.telegram.strategy.notification.book.BookDeletingNotificationService;
import book.store.telegram.strategy.notification.book.BookUpdatingNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AdminNotificationStrategy<Book> notificationStrategy;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH, INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify that getAll() method works as expected with valid pageable
            """)
    @Test
    public void getAll_ValidRequest_ReturnsRequiredBooks() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);

        String content = objectMapper.writeValueAsString(pageable);

        MvcResult result = mockMvc.perform(get("/books")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class
        );

        assertEquals(5, actual.length);
    }

    @Test
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void getAll_NonValidPageable_ReturnsEmptyList() throws Exception {
        Pageable pageable = PageRequest.of(15, 5);

        String content = objectMapper.writeValueAsString(pageable);

        MvcResult result = mockMvc.perform(get("/books")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class
        );

        assertEquals(0, actual.length);
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH, INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify that getById() method works as expected with valid id
            """)
    @Test
    public void getById_ValidId_ReturnsValidBook() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(get("/books/" + id))
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto.class
        );

        BookResponseDto expected = new BookResponseDto()
                .setId(1L)
                .setAuthor("Harper Lee")
                .setIsbn("9780061120084")
                .setPrice(BigDecimal.valueOf(10.99))
                .setDescription("A classic novel set in the American South during the 1930s.")
                .setTitle("To Kill a Mockingbird")
                .setCoverImage("to_kill_a_mockingbird.jpg")
                .setCategoriesIds(new HashSet<>());

        assertEquals(expected, actual);
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH, INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify that search() method works as expected with valid author
            """)
    @Test
    public void search_ValidInputParams_ReturnsValidBook() throws Exception {
        BookSearchParametersDto parametersDto = new BookSearchParametersDto(
                null,
                "Harper Lee",
                null,
                null,
                null
        );

        String content = objectMapper.writeValueAsString(parametersDto);

        MvcResult result = mockMvc.perform(get("/books/search")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class
        );

        BookResponseDto expected = new BookResponseDto()
                .setId(1L)
                .setAuthor("Harper Lee")
                .setIsbn("9780061120084")
                .setPrice(BigDecimal.valueOf(10.99))
                .setDescription("A classic novel set in the American South during the 1930s.")
                .setTitle("To Kill a Mockingbird")
                .setCoverImage("to_kill_a_mockingbird.jpg")
                .setCategoriesIds(new HashSet<>());

        assertEquals(expected, actual[0]);
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH, INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify that search() method works as expected with valid search params
            """)
    @Test
    public void search_ValidRequest_ReturnsValidResponse() throws Exception {
        BookSearchParametersDto parametersDto = new BookSearchParametersDto(
                "bird",
                null,
                "novel",
                List.of(BigDecimal.ZERO, BigDecimal.valueOf(15L)),
                new HashSet<>()
        );

        String content = objectMapper.writeValueAsString(parametersDto);

        MvcResult result = mockMvc.perform(get("/books/search")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class
        );

        BookResponseDto expected = new BookResponseDto()
                .setId(1L)
                .setAuthor("Harper Lee")
                .setIsbn("9780061120084")
                .setPrice(BigDecimal.valueOf(10.99))
                .setDescription("A classic novel set in the American South during the 1930s.")
                .setTitle("To Kill a Mockingbird")
                .setCoverImage("to_kill_a_mockingbird.jpg")
                .setCategoriesIds(new HashSet<>());

        assertEquals(expected, actual[0]);
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH,
                    DELETE_ALL_CATEGORIES_FILE_PATH,
                    INSERT_CATEGORY_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH,
                    DELETE_ALL_CATEGORIES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify that create() method works as expected with a valid request
            """)
    @Test
    @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
    public void create_ValidRequest_Success() throws Exception {
        BookCreateRequestDto requestDto = new BookCreateRequestDto(
                "To Kill a Mockingbird",
                "Harper Lee",
                "9780061120084",
                BigDecimal.valueOf(10.99),
                "A classic novel set in the American South during the 1930s.",
                "to_kill_a_mockingbird.jpg",
                Set.of(1L)
        );

        String content = objectMapper.writeValueAsString(requestDto);

        User user = new User();
        user.setRoles(Set.of(new Role(2L)));

        when(notificationStrategy.getNotificationService(
                anyString(), anyString())
        )
                .thenReturn(mock(BookCreationNotificationService.class));

        MvcResult result = mockMvc.perform(post("/books")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();

        BookResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto.class
        );

        BookResponseDto expected = fromRequestDto(1L, requestDto);

        assertEquals(expected, actual);
    }

    @DisplayName("""
            Verify that create() endpoint works as expected with a non-valid request
            """)
    @Test
    @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
    public void create_NonValidRequest_Failure() throws Exception {
        BookCreateRequestDto requestDto = new BookCreateRequestDto(
                "non-valid",
                "non-valid",
                "non-valid",
                BigDecimal.ZERO,
                "desc",
                "cov",
                new HashSet<>()
        );

        String content = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/books")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest());
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH,
                    DELETE_ALL_CATEGORIES_FILE_PATH,
                    INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that updateById() endpoint works as expected with a valid request
            """)
    @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
    @Test
    public void updateById_ValidRequest_Success() throws Exception {
        // updating a title only
        BookUpdateDto updateDto = new BookUpdateDto(
                "New title",
                null,
                null,
                null,
                null,
                null,
                null
        );

        Long id = 1L;

        String content = objectMapper.writeValueAsString(updateDto);

        when(notificationStrategy.getNotificationService(
                anyString(), anyString())
        )
                .thenReturn(mock(BookUpdatingNotificationService.class));

        MvcResult result = mockMvc.perform(put("/books/" + id)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto.class
        );

        assertEquals(updateDto.title(), actualResponse.getTitle());
    }

    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH,
                    DELETE_ALL_CATEGORIES_FILE_PATH,
                    INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that deleteById() endpoint works as expected with a valid request
            """)
    @WithMockUser(username = "admin@example.com", authorities = {"ROLE_ADMIN"})
    @Test
    public void deleteById_ValidRequest_Success() throws Exception {
        Long id = 1L;

        when(notificationStrategy.getNotificationService(
                anyString(), anyString())
        )
                .thenReturn(mock(BookDeletingNotificationService.class));
        // deleting a book
        mockMvc.perform(delete("/books/" + id))
                .andExpect(status().isNoContent());

        MvcResult result = mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andReturn();

        BookResponseDto[] response = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookResponseDto[].class
        );

        // expecting that after deleting one book there will be 4 left
        assertEquals(4, response.length);
    }

    private BookResponseDto fromRequestDto(Long id, BookCreateRequestDto requestDto) {
        return new BookResponseDto()
                .setIsbn(requestDto.isbn())
                .setPrice(requestDto.price())
                .setAuthor(requestDto.author())
                .setTitle(requestDto.title())
                .setId(id)
                .setDescription(requestDto.description())
                .setCoverImage(requestDto.coverImage())
                .setCategoriesIds(requestDto.categoriesIds()
                );
    }
}
