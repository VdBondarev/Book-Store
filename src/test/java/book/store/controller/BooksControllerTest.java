package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_BOOKS_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_BOOKS_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.book.BookResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                    INSERT_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(scripts =
            {
                    DELETE_ALL_BOOKS_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("""
            Verify that findAll() method works as expected with valid pageable
            """)
    @Test
    public void findAll_ValidRequest_ReturnsRequiredBooks() throws Exception {
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

    @DisplayName("""
            Verify that findAll() method works as expected with non-valid pageable
            """)
    @Test
    public void findAll_NonValidPageable_ReturnsEmptyList() throws Exception {
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
}
