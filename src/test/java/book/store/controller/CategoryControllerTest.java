package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_CATEGORIES_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_CATEGORY_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_FIVE_CATEGORIES_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.category.CategoryResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .build();
    }

    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH,
                    INSERT_CATEGORY_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that getCategoryById() endpoint works as expected with valid input param
            """)
    @Test
    public void getCategoryById_ValidId_Success() throws Exception {
        Long id = 1L;

        MvcResult result = mockMvc.perform(
                        get("/categories/" + id)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class
        );

        CategoryResponseDto expected = new CategoryResponseDto(
                1L,
                "First category name",
                "First category description"
        );

        assertEquals(expected, actual);
    }

    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("""
            Verify that getCategoryById() endpoint throws an exception
            when passing non-valid id
            """)
    @Test
    public void getCategoryId_InvalidId_ThrowsException() throws Exception {
        Long id = 10L;

        MvcResult result = mockMvc.perform(
                        get("/categories/" + id)
                )
                .andExpect(status().isNotFound())
                .andReturn();

        assertEquals(EntityNotFoundException.class, result.getResolvedException().getClass());
    }

    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH,
                    INSERT_FIVE_CATEGORIES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH
            },
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that getAll() endpoint works as expected
            """)
    @Test
    public void getAll_ValidRequest_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                        get("/categories")
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto[].class
        );

        int expectedLength = 5;
        assertEquals(expectedLength, actual.length);
    }
}

