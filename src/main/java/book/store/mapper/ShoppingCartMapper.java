package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shopping.cart.ShoppingCartResponseDto;
import book.store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "cartItems", ignore = true)
    ShoppingCartResponseDto toResponseDto(ShoppingCart shoppingCart);
}
