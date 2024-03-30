package book.store.service.category;

import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDto create(CreateCategoryRequestDto requestDto) {
        Category category = categoryMapper.toModel(requestDto);
        categoryRepository.save(category);
        return categoryMapper.toResponseDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .stream()
                .map(categoryMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDto getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a category by id " + id));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
