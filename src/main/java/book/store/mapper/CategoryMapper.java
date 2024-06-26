package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.category.CategoryResponseDto;
import book.store.dto.category.CategoryUpdateDto;
import book.store.dto.category.CreateCategoryRequestDto;
import book.store.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryResponseDto toResponseDto(Category category);

    Category toModel(CreateCategoryRequestDto requestDto);

    Category toModel(@MappingTarget Category category,
                     CategoryUpdateDto updateDto);
}
