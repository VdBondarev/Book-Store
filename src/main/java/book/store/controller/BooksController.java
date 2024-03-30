package book.store.controller;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookUpdateDto;
import book.store.service.book.BookService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BooksController {
    private final BookService bookService;

    @GetMapping
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookResponseDto create(@RequestBody BookCreateRequestDto requestDto) {
        return bookService.create(requestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookResponseDto update(
            @PathVariable Long id,
            @RequestBody BookUpdateDto updateDto) {
        return bookService.updateById(id, updateDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteById(@RequestParam(name = "book_id") Long id) {
        bookService.deleteById(id);
    }
}
