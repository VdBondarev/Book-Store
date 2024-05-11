package book.store.controller;

import static book.store.holder.LinksHolder.DELETE_ALL_CATEGORIES_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_CATEGORY_FILE_PATH;
import static book.store.holder.LinksHolder.INSERT_FIVE_CATEGORIES_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CategoryUpdateDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Category;
import book.store.telegram.strategy.notification.AdminNotificationStrategy;
import book.store.telegram.strategy.notification.category.CategoryCreationNotificationService;
import book.store.telegram.strategy.notification.category.CategoryUpdatingNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CategoryControllerTest {
    public static final String ADMIN = "ROLE_ADMIN";
    protected static MockMvc mockMvc;
    private static final String EMAIL = "admin@example.com";
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AdminNotificationStrategy<Category> notificationStrategy;

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

    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH
            }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH
            }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("""
            Verify that create() endpoint works as expected
            When passing a valid request dto
            """)
    @Test
    @WithMockUser(username = EMAIL, authorities = ADMIN)
    public void create_ValidRequest_Success() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                "Category name",
                "Category description"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        when(notificationStrategy.getNotificationService(anyString(), anyString()))
                .thenReturn(mock(CategoryCreationNotificationService.class));
        MvcResult result = mockMvc.perform(
                        post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isCreated())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class
        );

        CategoryResponseDto expected = new CategoryResponseDto(
                1L,
                "Category name",
                "Category description"
        );

        assertEquals(expected, actual);
    }

    @DisplayName("""
            Verify that create() endpoint works as expected
            When passing a non-valid request dto
            """)
    @Test
    @WithMockUser(username = EMAIL, authorities = ADMIN)
    public void create_NonValidRequest_ValidationIsNotPassed() throws Exception {
        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto(
                // does not start with a capital letter
                "non-valid",
                "non-valid"
        );

        String content = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        post("/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Sql(
            scripts = {
                    DELETE_ALL_CATEGORIES_FILE_PATH
            }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @DisplayName("""
            Verify that updateCategoryById() endpoint throws an exception when passing non-valid id
            """)
    @Test
    @WithMockUser(username = EMAIL, authorities = ADMIN)
    public void updateCategoryById_NonValidId_ThrowsException() throws Exception {
        Long id = 112515L;
        CategoryUpdateDto updateDto = new CategoryUpdateDto("Test", "Test");

        String content = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(
                put("/categories/" + id)
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
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
            Verify that updateCategoryById works as expected when passing valid params
            """)
    @Test
    @WithMockUser(username = EMAIL, authorities = ADMIN)
    public void updateCategoryById_ValidRequest_Success() throws Exception {
        Long id = 1L;
        CategoryUpdateDto updateDto = new CategoryUpdateDto("Updated", "Updated");

        String content = objectMapper.writeValueAsString(updateDto);

        when(notificationStrategy.getNotificationService(anyString(), anyString()))
                .thenReturn(mock(CategoryUpdatingNotificationService.class));

        MvcResult result = mockMvc.perform(
                        put("/categories/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content)
                )
                .andExpect(status().isOk())
                .andReturn();

        CategoryResponseDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CategoryResponseDto.class
        );

        CategoryResponseDto expected = new CategoryResponseDto(1L, "Updated", "Updated");

        assertEquals(expected, actual);
    }
}
/**
 *
 *     @PutMapping("/{id}")
 *     @PreAuthorize("hasRole('ROLE_ADMIN')")
 *     @Operation(summary = "Update an existing category",
 *             description = "Endpoint for updating a category in db. Allowed for admins only")
 *     public CategoryResponseDto updateCategoryById(
 *             @PathVariable Long id,
 *             @RequestBody @Valid CategoryUpdateDto updateDto) {
 *         return categoryService.updateById(id, updateDto);
 *     }
 *
 *     @DeleteMapping("/{id}")
 *     @PreAuthorize("hasRole('ROLE_ADMIN')")
 *     @ResponseStatus(HttpStatus.NO_CONTENT)
 *     @Operation(summary = "Delete an existing category",
 *             description = "Endpoint for deleting a category from db. Allowed for admins only")
 *     public void deleteById(
 *             @PathVariable Long id) {
 *         categoryService.deleteById(id);
 *     }
 * }
 */
/*
@Override
    public void deleteById(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            return;
        }
        categoryRepository.deleteById(id);
        sendMessage(TELEGRAM, CATEGORY_DELETING, null, new Category(id));
    }

    @Override
    public CategoryResponseDto updateById(Long id, CategoryUpdateDto updateDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a category by id " + id));
        category = categoryMapper.toModel(category, updateDto);
        categoryRepository.save(category);
        sendMessage(TELEGRAM, CATEGORY_UPDATING, null, category);
        return categoryMapper.toResponseDto(category);
    }
 */

