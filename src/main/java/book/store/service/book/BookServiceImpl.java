package book.store.service.book;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookUpdateDto;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.BookRepository;
import book.store.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final CategoryRepository categoryRepository;

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
        bookRepository.deleteById(id);
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
        return bookMapper.toResponseDto(book);
    }
}
