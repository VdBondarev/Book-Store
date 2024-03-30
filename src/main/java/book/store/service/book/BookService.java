package book.store.service.book;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;

public interface BookService {
    BookResponseDto create(BookCreateRequestDto requestDto);
}
