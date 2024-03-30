package book.store.controller;

import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.service.category.CategoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public CategoryResponseDto getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public List<CategoryResponseDto> getAll(Pageable pageable) {
        return categoryService.getAll(pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CategoryResponseDto create(@RequestBody CreateCategoryRequestDto requestDto) {
        return categoryService.create(requestDto);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(
            @RequestParam(name = "category_id") Long id) {
        categoryService.deleteById(id);
    }
}
