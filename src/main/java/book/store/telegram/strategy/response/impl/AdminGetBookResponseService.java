package book.store.telegram.strategy.response.impl;

import book.store.model.Book;
import book.store.model.Category;
import book.store.repository.BookRepository;
import book.store.telegram.strategy.response.AdminResponseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminGetBookResponseService implements AdminResponseService {
    private static final String BOOK_REGEX =
            "^(?i)Get info about a book with id:\\s*\\d+$";
    private final BookRepository bookRepository;

    @Override
    public String getMessage(String text) {
        Long id = getId(text);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "There is no book by id " + id));
        String message = """
                A new book is created.
                                
                Id: %,
                Title: %s,
                Author: %s,
                ISBN: %S,
                Description: %s,
                Cover image: %s,
                Categories ids: %s.
                """;
        return String.format(message,
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getCoverImage(),
                book.getCategories().stream().map(Category::getId).collect(Collectors.toList()));

    }

    @Override
    public boolean isApplicable(String text) {
        return text.matches(BOOK_REGEX);
    }
}
