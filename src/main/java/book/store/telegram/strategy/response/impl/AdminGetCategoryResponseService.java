package book.store.telegram.strategy.response.impl;

import book.store.model.Category;
import book.store.repository.CategoryRepository;
import book.store.telegram.strategy.response.AdminResponseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminGetCategoryResponseService implements AdminResponseService {
    private static final String CATEGORY_REGEX =
            "^(?i)Get info about a category with id:\\s*\\d+$";
    private final CategoryRepository categoryRepository;

    @Override
    public String getMessage(String text) {
        Long id = getId(text);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There is no category by id " + id));
        String message = """               
                ***
                Found this category.
                
                Id: %s,
                Name: %s,
                Description: %s.
                ***
                """;
        return String.format(
                message,
                category.getId(),
                category.getName(),
                category.getDescription());
    }

    @Override
    public boolean isApplicable(String text) {
        return text.matches(CATEGORY_REGEX);
    }
}
