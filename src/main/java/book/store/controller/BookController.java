package book.store.controller;

import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookSearchParametersDto;
import book.store.dto.book.BookUpdateDto;
import book.store.service.book.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Books controller", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books with pageable sorting")
    public List<BookResponseDto> getAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a book by id")
    public BookResponseDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Find books by params")
    public List<BookResponseDto> search(
            @RequestBody BookSearchParametersDto parametersDto,
            Pageable pageable) {
        return bookService.search(parametersDto, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Create a new book",
            description = "Endpoint for inserting a book into db. Allowed for admins only")
    public BookResponseDto create(@RequestBody @Valid BookCreateRequestDto requestDto) {
        return bookService.create(requestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Update an existing book",
            description = "Endpoint for updating a book in db. Allowed for admins only")
    public BookResponseDto update(
            @PathVariable Long id,
            @RequestBody @Valid BookUpdateDto updateDto) {
        return bookService.updateById(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Delete an existing book",
            description = "Endpoint for deleting a book from db. Allowed for admins only")
    public void deleteById(@PathVariable Long id) {
        bookService.deleteById(id);
    }
}
