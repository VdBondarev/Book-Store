package book.store.service.category;

import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CreateCategoryRequestDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    CategoryResponseDto create(CreateCategoryRequestDto requestDto);

    List<CategoryResponseDto> getAll(Pageable pageable);

    CategoryResponseDto getCategoryById(Long id);

    void deleteById(Long id);
}
