package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shopping.item.CartItemResponseDto;
import book.store.dto.shopping.item.CreateCartItemRequestDto;
import book.store.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    CartItemResponseDto toResponseDto(CartItem cartItem);

    CartItem toModel(CreateCartItemRequestDto requestDto);
}
