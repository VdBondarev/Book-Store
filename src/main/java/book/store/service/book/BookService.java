package book.store.service.book;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.BookUpdateDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDto create(BookCreateRequestDto requestDto);

    List<BookResponseDto> getAll(Pageable pageable);

    BookResponseDto getBookById(Long id);

    void deleteById(Long id);

    BookResponseDto updateById(Long id, BookUpdateDto updateDto);

    List<BookResponseDto> search(BookSearchParametersDto parametersDto, Pageable pageable);
}
