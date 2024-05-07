package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_BOOKS_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_BOOKS_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

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
}
