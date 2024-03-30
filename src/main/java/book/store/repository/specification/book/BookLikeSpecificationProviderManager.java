package book.store.repository.specification.book;

import book.store.model.Book;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookLikeSpecificationProviderManager
        implements LikeSpecificationProviderManager<Book, String> {
    private final List<LikeSpecificationProvider<Book, String>> providers;

    @Override
    public LikeSpecificationProvider<Book, String> getSpecificationProvider(String key) {
        return providers
                .stream()
                .filter(provider -> provider.getKey().equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find a specification provider for " + key));
    }
}
