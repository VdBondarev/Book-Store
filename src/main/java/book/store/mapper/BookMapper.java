package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.book.BookCreateRequestDto;
import book.store.dto.book.BookResponseDto;
import book.store.dto.book.BookUpdateDto;
import book.store.model.Book;
import book.store.model.Category;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface BookMapper {
    @Mapping(target = "categories", ignore = true)
    Book toModel(BookCreateRequestDto requestDto);

    @Mapping(target = "categories", ignore = true)
    Book toModel(@MappingTarget Book book, BookUpdateDto updateDto);

    @Mapping(target = "categoriesIds", ignore = true)
    BookResponseDto toResponseDto(Book book);

    @AfterMapping
    default void setCategoriesIds(
            @MappingTarget BookResponseDto responseDto,
            Book book) {
        Set<Long> categoriesIds = book.getCategories()
                .stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        responseDto.setCategoriesIds(categoriesIds);
    }

    @AfterMapping
    default void setCategories(
            @MappingTarget Book book,
            BookUpdateDto updateDto) {
        Set<Category> categories = updateDto.categoriesIds()
                .stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }

    @AfterMapping
    default void setCategories(
            @MappingTarget Book book,
            BookCreateRequestDto requestDto) {
        Set<Category> categories = requestDto.categoriesIds()
                .stream()
                .map(Category::new)
                .collect(Collectors.toSet());
        book.setCategories(categories);
    }
}
