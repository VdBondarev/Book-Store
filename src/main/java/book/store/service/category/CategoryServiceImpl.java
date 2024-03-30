package book.store.service.category;

import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CategoryUpdateDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.CategoryRepository;
import book.store.telegram.strategy.notification.AdminNotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private static final String TELEGRAM = "Telegram";
    private static final String CATEGORY_CREATION = "Category creation";
    private static final String CATEGORY_UPDATING = "Category updating";
    private static final String CATEGORY_DELETING = "Category deleting";
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final List<AdminNotificationService<Category>> notificationServices;

    @Override
    public CategoryResponseDto create(CreateCategoryRequestDto requestDto) {
        Category category = categoryMapper.toModel(requestDto);
        categoryRepository.save(category);
        sendMessage(TELEGRAM, CATEGORY_CREATION, null, category);
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

    private void sendMessage(
            String notificationService,
            String messageType,
            Long chatId,
            Category book) {
        notificationServices
                .stream()
                .filter(service -> service.isApplicable(notificationService, messageType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find a notification service for " + messageType))
                .sendMessage(chatId, book);
    }
}
