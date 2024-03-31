package book.store.service.book;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.BookUpdateDto;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.BookRepository;
import book.store.repository.CategoryRepository;
import book.store.repository.specification.book.BookSpecificationBuilder;
import book.store.telegram.strategy.notification.AdminNotificationService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private static final String TELEGRAM = "Telegram";
    private static final String BOOK_CREATION = "Book creation";
    private static final String BOOK_UPDATING = "Book updating";
    private static final String BOOK_DELETING = "Book deleting";
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;
    private final BookSpecificationBuilder bookSpecificationBuilder;
    private final List<AdminNotificationService<Book>> notificationServices;

    @Override
    public BookResponseDto create(BookCreateRequestDto requestDto) {
        requestDto.categoriesIds()
                .forEach(categoryId ->
                        categoryRepository
                                .findById(categoryId)
                                .orElseThrow(() -> new EntityNotFoundException(
                                        "Can't find a category by id " + categoryId)));
        Book book = bookMapper.toModel(requestDto);
        bookRepository.save(book);
        sendMessage(TELEGRAM, BOOK_CREATION, null, book);
        return bookMapper.toResponseDto(book);
    }

    @Override
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable)
                .stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookResponseDto getBookById(Long id) {
        return bookRepository.findById(id)
                .map(bookMapper::toResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a book by id " + id));
    }

    @Override
    public void deleteById(Long id) {
        if (bookRepository.findByIdWithoutCategories(id).isEmpty()) {
            return;
        }
        bookRepository.deleteById(id);
        sendMessage(TELEGRAM, BOOK_DELETING, null, new Book(id));
    }

    @Override
    public BookResponseDto updateById(Long id, BookUpdateDto updateDto) {
        if (updateDto.categoriesIds() != null && !updateDto.categoriesIds().isEmpty()) {
            updateDto.categoriesIds()
                    .forEach(categoryId -> categoryRepository.findById(categoryId)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    "Can't find a category by id " + categoryId)));
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a book by id " + id));
        book = bookMapper.toModel(book, updateDto);
        bookRepository.save(book);
        sendMessage(TELEGRAM, BOOK_UPDATING, null, book);
        return bookMapper.toResponseDto(book);
    }

    @Override
    public List<BookResponseDto> search(BookSearchParametersDto parametersDto, Pageable pageable) {
        Specification<Book> specification =
                bookSpecificationBuilder.build(parametersDto);
        return bookRepository.findAll(specification, pageable)
                .stream()
                .map(bookMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    private void sendMessage(
            String notificationService,
            String messageType,
            Long chatId,
            Book book) {
        notificationServices
                .stream()
                .filter(service -> service.isApplicable(notificationService, messageType))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "Can't find a notification service for " + messageType))
                .sendMessage(chatId, book);
    }
}
