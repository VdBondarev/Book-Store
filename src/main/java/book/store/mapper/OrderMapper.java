package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.OrderResponseDto;
import book.store.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(target = "orderItems", ignore = true)
    OrderResponseDto toResponseDto(Order order);
}
