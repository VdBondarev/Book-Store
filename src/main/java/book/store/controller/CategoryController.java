package book.store.controller;

import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CategoryUpdateDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Categories controller", description = "Endpoints for managing categories")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    @Operation(summary = "Get a category by id")
    public CategoryResponseDto getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    @Operation(summary = "Get all categories with pageable sorting")
    public List<CategoryResponseDto> getAll(Pageable pageable) {
        return categoryService.getAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new category",
            description = "Endpoint for inserting a category into db. Allowed for admins only")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponseDto create(@RequestBody @Valid CreateCategoryRequestDto requestDto) {
        return categoryService.create(requestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update an existing category",
            description = "Endpoint for updating a category in db. Allowed for admins only")
    public CategoryResponseDto updateCategoryById(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateDto updateDto) {
        return categoryService.updateById(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an existing category",
            description = "Endpoint for deleting a category from db. Allowed for admins only")
    public void deleteById(
            @PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
