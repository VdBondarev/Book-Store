package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.item.OrderItemResponseDto;
import book.store.model.CartItem;
import book.store.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    OrderItemResponseDto toResponseDto(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookId", source = "book.id")
    OrderItem toOrderItem(CartItem cartItem);
}
